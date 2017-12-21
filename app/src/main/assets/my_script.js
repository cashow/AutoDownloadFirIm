var isInstall = false;
var intervalId;
var mPassword = "";

function myAutoLoginAndInstall() {
    // 如果存在密码框，填入密码并登录
    if ($("#passwdField").length) {
        // 填入密码
        $("#passwdField").val(mPassword);
        // 登录
        FIR.confirmPasswd();
        return
    }
    // 如果存在下载按钮，开始下载
    if ($("#actions").length) {
        if (!isInstall) {
            isInstall = true;
            // 开始下载
            FIR.install();
            clearInterval(refreshIntervalId);
        }
    }
}

function mAutoLoginAndInstall(password) {
    mPassword = password;
    intervalId = setInterval(myAutoLoginAndInstall, 1000);
}