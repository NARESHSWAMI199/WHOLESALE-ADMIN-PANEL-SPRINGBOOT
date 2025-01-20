package com.sales.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.util.List;

//@Getter
//@Setter
//@AllArgsConstructor
//@Builder
@Data
public class MessageDto {

    private String message;
    private String sender;
    private String receiver;
    private String type;
    private String sessionId;
    private Long time;
    private Long lastSeen;
    private Boolean seen=false;
    private List<MultipartFile> images;
}
