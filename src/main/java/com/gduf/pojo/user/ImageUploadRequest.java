package com.gduf.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadRequest {
    private String fileName;
    private String fileType;
    private String file;
}
