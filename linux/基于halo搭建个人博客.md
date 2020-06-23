# 使用Nginx进行反向代理

下载安装Nginx的过程：[https://www.hyhwky.com/archives/linux%E7%B3%BB%E7%BB%9F%E9%83%A8%E7%BD%B2nginx](https://www.hyhwky.com/archives/linux系统部署nginx)

# nginx: [emerg] the "ssl" parameter requires ngx_http_ssl_module in /usr/local/nginx/conf/nginx.conf:106

参考：[https://www.cnblogs.com/ghjbk/p/6744131.html](https://www.cnblogs.com/ghjbk/p/6744131.html)

# cp: cannot create regular file ‘/usr/local/nginx/sbin/nginx’: Text file busy

覆盖nginx失败，可以查看nginx进程号，直接kill。

# 配置halo.conf

```bash
vim /etc/nginx/conf.d/halo.conf
```

```bash
server {
    listen 80;

    server_name hyhwky.com www.hyhwky.com;

    client_max_body_size 1024m;

    location / {
        proxy_set_header HOST $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        proxy_pass http://127.0.0.1:8090/;
    }
}

server {
    listen 443 ssl;

    server_name hyhwky.com www.hyhwky.com;
	# 配置证书的源，已经存放在/usr/local/nginx/conf/cert/下
    ssl_certificate /usr/local/nginx/conf/cert/4102621_www.hyhwky.com.pem;
    ssl_certificate_key /usr/local/nginx/conf/cert/4102621_www.hyhwky.com.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;

    location / {
        proxy_set_header HOST $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        proxy_pass http://127.0.0.1:8090/;
    }
}
```

```bash
nginx -t #检查配置是否有误
nginx -s reload #重载Nginx配置
```

# SSL证书下载

参考：[https://www.cnblogs.com/osfipin/p/freessl.html](https://www.cnblogs.com/osfipin/p/freessl.html)

# 配置SSL之后，为什么还是显示不安全

参考：[https://cloud.tencent.com/developer/article/1411026](https://cloud.tencent.com/developer/article/1411026)