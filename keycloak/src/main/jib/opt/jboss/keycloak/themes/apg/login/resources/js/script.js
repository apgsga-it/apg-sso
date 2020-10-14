function handleUsernameChange() {
    const username = document.getElementById("username").value;

    if (username && username !== "") {
        const resetPasswordLinkNew = document.getElementById("resetPasswordLinkHidden").href + "&username=" + encodeURI(username);
        document.getElementById("resetPasswordLink").href = resetPasswordLinkNew;

        const registrationLinkNew = document.getElementById("registrationLinkHidden").href + "&username=" + encodeURI(username);
        document.getElementById("registrationLink").href = registrationLinkNew;
    } else {
        document.getElementById("resetPasswordLink").href = document.getElementById("resetPasswordLinkHidden").href;
        document.getElementById("registrationLink").href = document.getElementById("registrationLinkHidden").href;
    }
}
