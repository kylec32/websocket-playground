package tk.kylecarter.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Register STOMP endpoints mapping each to a specific URL and (optionally)
     * enabling and configuring SockJS fallback options.
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
    }

    /**
     * Configure message broker options.
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));
        registry
                .setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user")
                .enableStompBrokerRelay("/topic", "/response")
                .setUserDestinationBroadcast("/topic/logbook-unresolved-user")
                .setUserRegistryBroadcast("/topic/logbook-user-registry")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setSystemHeartbeatReceiveInterval(30000)
                .setSystemHeartbeatSendInterval(30000);

    }

    /**
     * Configure the {@link MessageChannel} used for
     * incoming messages from WebSocket clients. By default the channel is backed
     * by a thread pool of size 1. It is recommended to customize thread pool
     * settings for production use.
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            /**
             * Invoked before the Message is actually sent to the channel.
             * This allows for modification of the Message if necessary.
             * If this method returns {@code null} then the actual
             * send invocation will not occur.
             *
             * @param message
             * @param channel
             */
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String user = accessor.getFirstNativeHeader("Auth-User");
                    accessor.setUser(new ChatPrincipal(user));
                }

                return message;
            }
        });
    }

}
