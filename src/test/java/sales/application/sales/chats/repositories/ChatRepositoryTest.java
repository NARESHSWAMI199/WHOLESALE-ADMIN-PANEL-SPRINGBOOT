package sales.application.sales.chats.repositories;

import com.sales.SalesApplication;
import com.sales.entities.Chat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.util.TestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SalesApplication.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChatRepositoryTest extends TestUtil {

    @Autowired
    private com.sales.chats.repositories.ChatRepository chatRepository;

    @Test
    public void testGetChatBySenderKeyOrReceiverKey_and_unseenCount_and_parentLookup() {
        String senderSlug = "s-" + java.util.UUID.randomUUID();
        String receiverSlug = "r-" + java.util.UUID.randomUUID();

        // create two sample chats
        long now = System.currentTimeMillis();
        Chat c1 = Chat.builder()
                .sender(senderSlug)
                .receiver(receiverSlug)
                .message("hello")
                .createdAt(now)
                .seen(false)
                .isSent("S")
                .build();

        Chat c2 = Chat.builder()
                .sender(receiverSlug)
                .receiver(senderSlug)
                .message("reply")
                .createdAt(now + 1000)
                .seen(false)
                .isSent("S")
                .build();

        chatRepository.save(c1);
        chatRepository.save(c2);

        List<Chat> chats = chatRepository.getChatBySenderKeyOrReceiverKey(senderSlug, receiverSlug);
        assertThat(chats).isNotEmpty();

        Integer unseenCount = chatRepository.getUnSeenChatsCount(senderSlug, receiverSlug);
        assertThat(unseenCount).isNotNull();

        // parent lookup by exact createdAt
        var opt = chatRepository.getParentMessageByCreateAt(senderSlug, receiverSlug, now);
        assertThat(opt).isPresent();
        assertThat(opt.get().getMessage()).isEqualTo("hello");

        Integer id = chatRepository.getParentMessageIdByCreateAt(senderSlug, receiverSlug, now);
        assertThat(id).isNotNull().isGreaterThan(0);
    }
}
