var isInstall = false;

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
        }
    }
}

function mAutoLoginAndInstall(password) {
    setInterval(myAutoLoginAndInstall(password), 1000);
}