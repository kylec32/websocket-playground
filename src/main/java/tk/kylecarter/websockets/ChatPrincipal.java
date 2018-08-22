package tk.kylecarter.websockets;

import java.security.Principal;

public class ChatPrincipal implements Principal {

    private String userName;

    public ChatPrincipal(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    @Override
    public String getName() {
        return userName;
    }
}
