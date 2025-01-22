package com.sales.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@Getter
//@Setter
//@AllArgsConstructor
//@Builder
@Data
public class MessageDto {

    private Integer id;
    private String message;
    private String sender;
    private String receiver;
    private String type;
    private String sessionId;
    private Long createdAt;
    private Long lastSeen;
    private Boolean seen=false;
    private List<MultipartFile> images;
    private List<String> imagesUrls;
    private String isDeleted;
}
