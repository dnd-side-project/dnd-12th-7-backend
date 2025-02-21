package com.dnd.moddo.domain.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.dto.TempImageResponse;
import com.dnd.moddo.domain.image.service.CommandImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
	private final CommandImageService commandImageService;

	@PostMapping("/temp")
	public ResponseEntity<TempImageResponse> saveTempImage(@RequestParam("file") List<MultipartFile> files) {
		TempImageResponse uniqueKey = commandImageService.uploadTempImage(files);
		return ResponseEntity.ok(uniqueKey);
	}

	@PostMapping("/update")
	public ResponseEntity<ImageResponse> updateImage(@RequestParam("uniqueKey") List<String> uniqueKeys) {
		ImageResponse finalImagePath = commandImageService.uploadFinalImage(uniqueKeys);
		return ResponseEntity.ok(finalImagePath);
	}
}

