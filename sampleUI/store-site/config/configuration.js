module.exports = {

    Identity_API_Host: "54.186.20.200",
    Identity_API_Port: 8081,

    Catalog_API_Host: "54.186.20.200",
    Catalog_API_Port: 8083,

    Cart_API_Host: "54.186.20.200",
    Cart_API_Port: 8082,

    CookiesTimeout: 10 * 60 * 1000,

    // API Data field and other site post filed
    FieldNames: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IdTokenUserId: "userId",
        Username: "userName",

        TokenInfoUserId: "sub",
        ProfileFirstname: "firstName",
        ProfileLastname: "lastName"
    },

    QueryStrings: {
        Code: "code",
        IdToken: "id_token",
        AccessToken: "access_token"
    },
    SaveQueryStringArray: ["id_token", "access_token"],

    SessionKeys: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IsAuthenticate: "is_auth",
        UserId: "user_id",
        Username: "username"
    },

    LoginBackUrl: "http://10.0.1.143:3100/auth/login",
    LoginUrl: "http://54.186.20.200:8081/rest/authorize?client_id=client&response_type=code&redirect_uri=http://10.0.1.143:3100/auth/login&scope=openid%20identity&nonce=123",
    //LoginUrl: "http://127.0.0.1:3000?fs=flow_state&redirect_url=http://localhost:3100/auth/login",
    RegisterUrl: "http://10.0.1.143:3000/?redirect_url=http://10.0.1.143:3100/auth/register#/register"

};