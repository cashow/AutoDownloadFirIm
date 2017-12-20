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
