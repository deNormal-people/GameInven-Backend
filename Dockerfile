# Java 17 기반 slim 이미지 사용
FROM openjdk:17-jdk-slim

# 환경 변수 설정 (옵션)
ENV TZ=Asia/Seoul
ENV JAVA_OPTS=""

# JAR 파일을 app.jar로 복사
COPY *.jar app.jar

# 네트워크 포트 오픈 (Spring Boot 기본 포트 8080)
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
