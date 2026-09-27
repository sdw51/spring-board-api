# Spring Board API

회원가입, 로그인, 게시글과 댓글 CRUD를 구현한 게시판 API입니다.

Spring Boot 3.5.16, JDK 21, Gradle 8.14.3을 사용합니다.

## 실행 방법

JDK 21이 필요합니다. H2 메모리 DB를 사용하므로 DB를 따로 설치할 필요는 없습니다. 서버를 종료하면 데이터는 사라집니다.

```powershell
# Windows
.\gradlew.bat bootRun
```

```bash
# macOS / Linux
./gradlew bootRun
```

기본 주소는 `http://localhost:8080`입니다. H2 콘솔은 `/h2-console`에서 접속할 수 있습니다.

- JDBC URL: `jdbc:h2:mem:db`
- 사용자: `sa`, 비밀번호: 빈 값

## API 명세

요청 본문은 JSON입니다. 인증이 필요한 요청에는 로그인할 때 받은 `JSESSIONID` 쿠키를 보냅니다.
아래 상태 코드는 정상 요청과 서비스에서 처리하는 주요 오류 기준입니다.

| 기능 | 메서드 / 주소 | 인증 | 요청 본문 | 성공 응답 | 상태 코드 |
|---|---|---|---|---|---|
| 가입 | POST `/v1/members/signup` | X | `email`, `password`, `nickName` | 가입 정보 | 200, 400 |
| 로그인 | POST `/v1/members/login` | X | `email`, `password` | 회원 정보 | 200, 400 |
| 글 작성 | POST `/v1/posts/create` | O | `title`, `content` | 게시글 | 200, 400, 401, 404 |
| 글 목록 | GET `/v1/posts?page=0&size=10` | X | 없음 | 게시글 목록 페이지 | 200 |
| 글 상세 | GET `/v1/posts/{postId}` | X | 없음 | 게시글 | 200, 404 |
| 글 수정 | PATCH `/v1/posts/{postId}` | O, 작성자 | `title`, `content` | 게시글 | 200, 400, 401, 404 |
| 글 삭제 | DELETE `/v1/posts/{postId}` | O, 작성자 | 없음 | 빈 본문 | 200, 401, 403, 404, 500 |
| 댓글 작성 | POST `/v1/comments/posts/{postId}` | O | `content` | 댓글 | 200, 400, 401, 404 |
| 댓글 목록 | GET `/v1/comments/posts/{postId}` | X | 없음 | 댓글 배열 | 200 |
| 댓글 수정 | PATCH `/v1/comments/{commentId}` | O, 작성자 | `content` | 댓글 | 200, 400, 401, 403, 404 |
| 댓글 삭제 | DELETE `/v1/comments/{commentId}` | O, 작성자 | 없음 | 빈 본문 | 200, 401, 403, 404 |

응답 필드는 다음과 같습니다. 날짜는 ISO 형식 문자열입니다.

| 응답 | 필드 |
|---|---|
| 가입 정보 | `id`, `email`, `nickName`, `createdAt` |
| 회원 정보 | `id`, `email`, `nickName` |
| 게시글 | `id`, `title`, `content`, `nickName`, `createdAt` |
| 게시글 목록 항목 | `id`, `title`, `nickname`, `createdAt`, `commentCount` |
| 댓글 | `id`, `content`, `nickName`, `createdAt`, `updatedAt` |

닉네임 필드의 대소문자는 현재 응답 기준입니다. 글 목록은 최신순이며 페이지 번호는 0부터 시작합니다. 댓글 목록은 작성순이고, 없는 게시글을 조회해도 빈 배열을 반환합니다. 글 수정은 제목과 내용을 모두 보내야 합니다.

오류 응답은 별도 JSON 포맷 없이 메시지 문자열을 반환합니다. 입력 검증 실패는 첫 번째 오류 메시지를 반환합니다.

```text
400 Bad Request
이미 사용 중인 이메일입니다.

401 Unauthorized
(본문 없음)

403 Forbidden
댓글 삭제 권한이 없습니다.

404 Not Found
게시글을 찾을 수 없습니다.
```

현재 다른 사람의 글 수정은 403이 아닌 400을 반환합니다. 처리하지 않은 서버 오류는 Spring Boot 기본 JSON 응답을 사용합니다.

## 설계

- **로그인:** 단일 서버에서 인증 상태를 간단히 관리할 수 있는 세션 방식을 사용했습니다. 로그인 성공 시 Spring Security 인증 정보를 세션에 저장하고, 이후에는 쿠키로 인증합니다. 비밀번호는 BCrypt로 해시해서 저장합니다.
- **관계:** 회원 한 명이 여러 글과 댓글을 작성할 수 있고, 글 하나에 여러 댓글이 달립니다. `Post → Member`, `Comment → Member`, `Comment → Post`는 모두 지연 로딩하는 다대일 관계입니다.
- **N+1:** 글 목록은 JPQL에서 작성자 닉네임과 댓글 수를 함께 조회해 DTO로 바로 반환합니다. 글마다 작성자나 댓글을 따로 조회하지 않습니다. 댓글 목록은 아직 작성자 조회에서 N+1이 생길 수 있습니다.
- **글 삭제:** 현재 댓글 자동 삭제는 구현하지 않았습니다. 댓글이 남아 있으면 외래 키 제약으로 글 삭제가 실패해 500을 반환합니다. 댓글이 없는 글만 삭제할 수 있습니다.

## 실행 결과

로컬 서버에서 HTTP 클라이언트로 확인한 결과입니다. 아래 curl은 같은 요청을 재현하는 Bash 명령이며, 기본 포트 8080 기준입니다. 확인 당시에는 18080을 사용했습니다. 새 DB에서 순서대로 실행하며, ID와 시간은 실행마다 달라집니다. 응답 헤더는 생략했습니다.

### 1. 회원가입

```bash
curl -i localhost:8080/v1/members/signup -H 'Content-Type: application/json' -d '{"email":"user@example.com","password":"1234","nickName":"user"}'
```

```http
HTTP/1.1 200

{"id":1,"email":"user@example.com","nickName":"user","createdAt":"2026-09-27T23:10:25.823062"}
```

### 2. 로그인

`-c`로 쿠키를 저장하고 이후 요청에서 `-b`로 전달합니다.

```bash
curl -i -c cookies.txt localhost:8080/v1/members/login -H 'Content-Type: application/json' -d '{"email":"user@example.com","password":"1234"}'
```

```http
HTTP/1.1 200

{"id":1,"email":"user@example.com","nickName":"user"}
```

### 3. 글 작성

```bash
curl -i -b cookies.txt localhost:8080/v1/posts/create -H 'Content-Type: application/json' -d '{"title":"Hello","content":"First post"}'
```

```http
HTTP/1.1 200

{"id":1,"title":"Hello","content":"First post","nickName":"user","createdAt":"2026-09-27T23:10:26.158903"}
```

### 4. 댓글 작성

```bash
curl -i -b cookies.txt localhost:8080/v1/comments/posts/1 -H 'Content-Type: application/json' -d '{"content":"First comment"}'
```

```http
HTTP/1.1 200

{"id":1,"content":"First comment","nickName":"user","createdAt":"2026-09-27T23:10:26.239665","updatedAt":"2026-09-27T23:10:26.239665"}
```

### 5. 글 목록 조회

```bash
curl -i 'localhost:8080/v1/posts?page=0&size=10'
```

```http
HTTP/1.1 200

{"content":[{"id":1,"title":"Hello","nickname":"user","createdAt":"2026-09-27T23:10:26.158903","commentCount":1}],"empty":false,"first":true,"last":true,"number":0,"numberOfElements":1,"pageable":{"offset":0,"pageNumber":0,"pageSize":10,"paged":true,"sort":{"empty":true,"sorted":false,"unsorted":true},"unpaged":false},"size":10,"sort":{"empty":true,"sorted":false,"unsorted":true},"totalElements":1,"totalPages":1}
```

### 6. 로그인 없이 글 작성 → 401

```bash
curl -i localhost:8080/v1/posts/create -H 'Content-Type: application/json' -d '{"title":"Hello","content":"First post"}'
```

```http
HTTP/1.1 401

```

응답 본문은 없습니다.

### 7. 다른 회원의 댓글 삭제 → 403

다른 계정으로 가입하고 로그인합니다.

```bash
curl -i localhost:8080/v1/members/signup -H 'Content-Type: application/json' -d '{"email":"other@example.com","password":"1234","nickName":"other"}'
curl -i -c other-cookies.txt localhost:8080/v1/members/login -H 'Content-Type: application/json' -d '{"email":"other@example.com","password":"1234"}'
```

가입과 로그인은 각각 200을 반환합니다.

```json
{"id":2,"email":"other@example.com","nickName":"other","createdAt":"2026-09-27T23:10:26.463894"}
```

```json
{"id":2,"email":"other@example.com","nickName":"other"}
```

이 계정으로 첫 번째 회원의 댓글을 삭제하면 거절됩니다.

```bash
curl -i -X DELETE -b other-cookies.txt localhost:8080/v1/comments/1
```

```http
HTTP/1.1 403

댓글 삭제 권한이 없습니다.
```
