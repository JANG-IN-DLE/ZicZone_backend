#베이스 이미지로 OpenJDK 17 사용
FROM openjdk:17

#빌드 과정에서 생성된 JAR 파일을 지정하는 ARG 변수
ARG JAR_FILE=build/libs/ziczone-0.0.1-SNAPSHOT.jar

#빌드된 JAR 파일을 컨테이너 이미지의 app.jar로 복사
COPY ${JAR_FILE} app.jar

#애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
