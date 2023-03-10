# Election





## 使用docker在本地运行项目

推荐使用创建应用的网络空间，并将应用加入网络空间进行启动。

#### 前置工作

#### 你必须有docker运行环境，并创建应用的网络空间

```
docker create network NETWORKNAME（名称自拟）
```



### 启动Mysql

#### 1、搜索mysql镜像并拉取最新镜像

```
docker search mysql

docker pull mysql
```



#### 2、启动MySQL

**需要在E:/docker-mysql/conf/创建一个my.cnf，并与容器内目录/etc/mysql/的my.cnf文件进行映射，持久化配置文件，宿主机目录即E:/docker-mysql/conf/可自定义。**

**需要创建E:/docker-mysql/data/目录，并与容器内目录/var/lib/mysql进行映射，持久化数据，宿主机目录即E:/docker-mysql/data可自定义。**

启动命令如下:

```
docker run -p 3306:3306 --name mysql-server --network election-network -v E:/docker-mysql/conf/my.cnf:/etc/mysql/my.cnf  -v E:/docker-mysql/data:/var/lib/mysql  -e MYSQL_ROOT_PASSWORD=123456 -d mysql 
```



以link模式启动（无需--network参数）

```
docker run -p 3306:3306 --name mysql-server -v E:/docker-mysql/conf/my.cnf:/etc/mysql/my.cnf  -v E:/docker-mysql/data:/var/lib/mysql  -e MYSQL_ROOT_PASSWORD=123456 -d mysql 
```





### 启动Redis

#### 1、搜索Redis镜像并拉取最新镜像

```
docker search redis

docker pull redis
```



#### 2、启动Redis

**需要在E:/docker-redis/conf/创建一个redis.conf，并与容器内目录/etc/redis/的redis.conf文件进行映射，持久化配置文件，宿主机目录即E:/docker-redis/conf/可自定义。**

**需要创建E:/docker-redis/data/目录，并与容器内目录/data进行映射，持久化数据，宿主机目录即E:/docker-redis/data/可自定义。**

启动命令如下:



```
docker run -d --name redis-server --network election-network -p 6379:6379 -v E:/docker-redis/conf/redis.conf:/etc/redis/redis.conf -v E:/docker-redis/data/:/data redis /etc/redis/redis.conf --appendonly yes --requirepass "123456" 
```



以link模式启动（无需--network参数）

```
docker run -d --name redis-server-p 6379:6379 -v E:/docker-redis/conf/redis.conf:/etc/redis/redis.conf -v E:/docker-redis/data/:/data /etc/redis/redis.conf --appendonly yes --requirepass "123456" redis
```







### 启动Eleciton应用

#### 1、通过idea将应用打包成jar

**注意：我们需要用maven将项目打成jar包，mvn package命令即可实现，需要配合maven的打包插件，详情可查询有关资料。**

```
mvn package
```



#### 2、编写dockerfile并打包成应用镜像



**①通过配置容器ip直接访问。**

```
#需要jdk17的基础镜像环境，如果你仓库没有现有的镜像，docker会去匹配最佳镜像并自动pull
FROM openjdk:17

#将jar包复制到容器指定目录
ADD Election-0.0.1-SNAPSHOT.jar /opt/app.jar

#环境变量，REDIS_NET为Redis的虚拟地址，MYSQL_NET为Mysql的虚拟地址
ENV REDIS_NET=xxx.xx.xx.xx MYSQL_NET=xxx.xx.xx.xx

#运行jar的命令
ENTRYPOINT ["java","-jar","/opt/app.jar"]

#RUN  cd /opt && ls -l
```

如何获取他们的虚拟地址？前面我们已经将Redis与Mysql运行起来了，使用以下命令分别获取到他们的虚拟地址。

```
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' xxxid  (容器id)
```

然后写入到dockerfile的REDIS_NET字段和MYSQL_NET字段。







**②使用容器别名访问**

别名访问必须**是--link或 需要通信的容器在同一个网络空间中。**后面启动命令会作详细解释



如：mysql的别名是mysql-sql，redis的别名是redis-server

dockerfile作如下更改：

```

#环境变量，REDIS_NET为Redis的虚拟地址，MYSQL_NET为Mysql的虚拟地址
ENV REDIS_NET=redis-server MYSQL_NET=mysql-server

```





#### 3、打包成docker images

我们将打包好的jar包与dockerfile放到一个目录下，并在这个目录下运行命令：

```
 docker build  -t election .
```





### 4、最后一步了，run起来！



①如果使用的是自己创建网络空间 即 docker create network **NETWORKNAME**

```
docker run --name my-election  --network NETWORKNAME -p 8080:8080 -d election 
```



②使用--link命令启动

> **--link的格式：**
>
> --link <name or id>:alias
>
> 其中，name和id是源容器的name和id，alias是源容器在link下的别名。

```
docker run --name my-election --link mysql:mysql-server --link redis:redis-server  -p 8080:8080 -d election 
```

这样无需执行`docker create network NETWORKNAME`，即可通过默认bridge的方式进行通信



③直接访问容器ip启动

这种方案，此前已经在dockerfile配置了redis容器ip和mysql容器ip

```
docker run --name my-election  -p 8080:8080 -d election 
```

