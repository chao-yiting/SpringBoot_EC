package com.example.demo.service.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductsRepository;
import com.example.demo.request.ProductsRequest;

@Service
public class AdminProductDetailService {
	@Autowired //依存性注入
	private ProductsRepository productsRepository; //ProductsRepository クラスを使えるようにする

	//商品をIDで検索する
	public Products findById(Long productId) {
		// ProductRepositoryを使用して商品をIDで検索し、存在しない場合はnullを返す
		return productsRepository.findById(productId).orElse(null); // 必要に応じてエラーハンドリングを追加
	}

	// 商品情報をIDで検索し、受け取った編集後の情報で更新する
	public void updateProduct(Products product, ProductsRequest productsRequest/*, MultipartFile imageFile*/)
		//	throws IOException 
	{
		// 商品情報をIDで検索し、存在する場合のみ更新を試みる
		productsRepository.findById(product.getId()).ifPresent(existingProduct -> {
			existingProduct.setName(productsRequest.getName());
			existingProduct.setCategoryId(Integer.parseInt(productsRequest.getCategoryId()));
			existingProduct.setDescription(productsRequest.getDescription());
			existingProduct.setPrice(productsRequest.getPrice());
			existingProduct.setStock(productsRequest.getStock());
			// 画像パスを設定
			//existingProduct.setImagePath(imagePath);

			/*/ 新しい画像がアップロードされた場合の処理
			if (imageFile != null && !imageFile.isEmpty()) {
				try {
					// 元の画像を削除する
					deleteImage(existingProduct.getImagePath());
					// 新しい画像を保存する
					String imagePath = saveImage(imageFile);
					// 商品情報を更新する際、新しい画像のパスを設定する
					existingProduct.setImagePath(imagePath);
				} catch (IOException e) {
					e.printStackTrace();
					// 画像の保存に失敗した場合は何らかのエラーハンドリングを行う
				}
			} else {
				// 新しい画像がアップロードされない場合は、画像の保存を行わずに既存の画像パスを使用する
				existingProduct.setImagePath(existingProduct.getImagePath());
			}*/

			// 更新をデータベースに保存
			productsRepository.save(existingProduct);
		});
	}

	// 商品をIDで削除するメソッド
	public void deleteProduct(Long productId) {
		productsRepository.findById(productId).ifPresent(product -> {

			try {
				// 画像ファイルを削除する
				deleteImage(product.getImagePath());
			} catch (IOException e) {
				e.printStackTrace();
				// 画像の保存に失敗した場合は何らかのエラーハンドリングを行う
			}
		});
		// 指定したIDの商品を削除
		productsRepository.deleteById(productId);

	}

	// 画像を保存するメソッド
	public String saveImage(MultipartFile imageFile) throws IOException {
		// 画像を保存するディレクトリのパスを設定
		String uploadDir = "./src/main/resources/static/img/upload_img";//
		// アップロードされた画像のファイル名を取得
		String originalFileName = imageFile.getOriginalFilename();
		// ファイル名にタイムスタンプを追加して一意にする
		String fileName = System.currentTimeMillis() + "_" + originalFileName;
		// 保存先のパスを作成
		Path uploadPath = Paths.get(uploadDir);
		// ディレクトリが存在しない場合は作成する
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// ファイルを指定のパスに保存する
		Path filePath = uploadPath.resolve(fileName);
		Files.copy(imageFile.getInputStream(), filePath);

		// 保存した画像のパスを返す
		return "/img/upload_img/" + fileName;
		//return uploadDir + "/" + fileName;
	}

	// 画像を削除するメソッド
	public void deleteImage(String imagePath) throws IOException {
		// 画像のパスからファイルを取得
		Path imagePathToDelete = Paths.get("./src/main/resources/static" + imagePath);
		// ファイルが存在し、削除に成功した場合はtrueを返す
		boolean isDeleted = Files.deleteIfExists(imagePathToDelete);
		// 削除に失敗した場合はエラーメッセージをログに出力
		if (!isDeleted) {
			System.err.println("Failed to delete image: " + imagePath);
		}
	}
}
