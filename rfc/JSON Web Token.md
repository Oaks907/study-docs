[在吗？认识一下JWT(JSON Web Token) ？](https://my.oschina.net/u/4062805/blog/3194697)

**JWT是一个JSON信息传输的开放标准，它可以使用密钥对信息进行数字签名，以确保信息是可验证和可信任的。**

## **JWT的结构是什么？**

JWT由三部分构成：**header（头部）、payload（载荷）和signature（签名**）。 以紧凑的形式由这三部分组成，由“.“分隔。

因此，JWT通常如下所示。

```
xxxxx.yyyyy.zzzzz
```

让我们把这串奇奇怪怪的东西分解开来：

**header**

header*通常*由两部分组成：令牌的类型（即JWT）和所使用的签名算法，例如HMAC SHA256或RSA等等。

例如：

```
{  "alg": "HS256", "typ": "JWT" }
```

显而易见，这货是一个json数据，然后这货会被**Base64**编码形成JWT的第一部分，也就是`xxxxx.yyyyy.zzzzz`中的**xxxxxx**。

**Payload**

这货是JWT的第二部分，叫载荷(负载)，内容也是一个json对象，它是存放有效信息的地方，它可以存放JWT提供的现成字段 :

- iss: 该JWT的签发者。
- sub: 该JWT所面向的用户。
- aud: 接收该JWT的一方。
- exp(expires): 什么时候过期，这里是一个Unix时间戳。
- iat(issued at): 在什么时候签发的。

举个例子：

```
{
  "iss": "www.baidu.com",
  "sub": "you",
  "aud": "me",
  "name": "456",
  "admin": true,
  "iat": 1584091337,
  "exp": 1784091337,
}
```

这货同样会被**Base64**编码，然后形成JWT的第二部分，也就是`xxxxx.yyyyy.zzzzz`中的**yyyyyy**。

**Signature**

这是JWT的第三部分，叫做签名，此部分用于防止JWT内容被篡改。将上面的两个编码后的字符串都用英文句号.连接在一起（头部在前），就形成了

```
xxxxxx.yyyyyy
```

然后再使用header中声明签名算法进行签名。 如果要使用HMAC SHA256算法，则将通过以下方式创建签名：

```
HMACSHA256( base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

当然，在加密的时候，我们还需要提供一个密钥（secret），我们可以自己随意指定。这样就形成了JWT的第三部分，也就是`xxxxx.yyyyy.zzzzz`中的**zzzzzz**。

最后，我们把这三个部分拼在一起，就形成了一个完整的JWT。