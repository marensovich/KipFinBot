package org.marensovich.Utils.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {

    public String help_reply;
    public String error_access_to_command_auth;

    public String auth_logged;
    public String auth_errorInput;
    public String auth_errorContact;
    public String auth_writeLogin;
    public String auth_writePassoword;
    public String auth_sendPhone;
    public String auth_chechData;
    public String logout_reply;
    public String logout_quit_success;
    public String logout_noAuth;
    public String start_reply;
    public String support_reply;
    public String support_linkText;
    public String support_buttonText;
    public String auth_success;
    public String auth_success1;
    public String auth_cancel;
    public String start;
    public String help;
    public String error;

}
