# 使用 JDK 17 slim 作为基础镜像
FROM openjdk:17-slim

# 安装 tzdata 以支持时区设置
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 设置时区环境变量
ENV TZ=Asia/Shanghai

# 设置工作目录
WORKDIR /app

# 拷贝 jar 包到镜像中，假设 jar 文件在 target 目录
COPY target/cloudsaver-1.0.0.jar /app/cloudsaver.jar

#COPY database.sqlite /app/database.sqlite

# 暴露端口
EXPOSE 8009

# 设置启动命令，通过 shell 执行以支持日志重定向
ENTRYPOINT ["/bin/sh", "-c", "java -jar -Dspring.profiles.active=uat /app/cloudsaver.jar > /dev/null 2> /app/biz.log"]
