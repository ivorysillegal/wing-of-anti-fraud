package com.gduf.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadRequest {
    private Integer userId;
    private String fileName;
    private String file;
}
