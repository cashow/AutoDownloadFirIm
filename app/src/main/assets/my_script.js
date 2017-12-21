var isInstall = false;
var refreshIntervalId;

function myAutoLogin(password) {
  setTimeout(mAutoLogin(password), 500);
}

function mAutoLogin(password) {
  $("#passwdField").val(password);
  FIR.confirmPasswd();
}

function myAutoInstall() {
  setTimeout(FIR.install(), 500);
}

function myAutoLoginAndInstall(password) {
    if ($("#passwdField").length) {
        mAutoLogin(password)
        return
    }
    if ($("#actions").length) {
        if (!isInstall) {
            isInstall = true;
            FIR.install();
            clearInterval(refreshIntervalId);
        }
    }
}

function mAutoLoginAndInstall(password) {
    refreshIntervalId = setInterval(myAutoLoginAndInstall(password), 1000);
}