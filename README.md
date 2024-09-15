# screenwiper_back
효율적인 스크린샷 관리를 위한 앱 서비스

## 작업 규칙
- 브랜치 파서 작업 후,
- `main` 브랜치로 PR 날려서 머지
<br/><br/>

## 기타 세팅 내용
### 1️⃣ properties 파일 서브모듈로 관리
properties 파일은 submodule 이용하여 여기서 관리하며,
해당 파일 수정해야 할 경우 아래 과정을 진행해야 합니다.

> ✔️ 수정 방법
> 1. properties 관리 레포에서 파일 수정
> 2. `git submodule update --remote` 명령어로 서브모듈 최신화
> 3. 최신화 내용 커밋

### 2️⃣ Docker로 CI/CD 배포룰 세팅
- main 브랜치에서 CI/CD Actions 동작합니다.
  - `Dockerfile .jar` 파일 복사 및 실행
  - `.github/workflows/deploy.yml` main 브랜치에 푸시 이벤트 발생 시, 서버에 배포