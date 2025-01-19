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
public class AdminProductCreateService {
	@Autowired //依存性注入
	private ProductsRepository productsRepository; //ProductsRepository クラスを使えるようにする

	//新しい商品情報をデータベースに保存する
	public void saveProduct(ProductsRequest productsRequest/*, String imagePath*/ /*MultipartFile imageFile*/) { //メソッドの宣言部分。void なので、値を返さない

		Products addProduct = new Products();
		addProduct.setName(productsRequest.getName());
		addProduct.setCategoryId(Integer.parseInt(productsRequest.getCategoryId()));
		addProduct.setDescription(productsRequest.getDescription());
		addProduct.setPrice(productsRequest.getPrice());
		addProduct.setStock(productsRequest.getStock());
		// 画像パスを設定
		//addProduct.setImagePath(imagePath);
		/*/ 画像ファイルがアップロードされている場合は、保存する
		if (!imageFile.isEmpty()) {
			try {
				String imagePath = saveImage(imageFile);
				addProduct.setImagePath(imagePath);
			} catch (IOException e) {
				// 画像保存中のエラー処理
				e.printStackTrace();
			}
		}*/

		//新しい商品情報（addProduct パラメータで受け取った商品情報）をデータベースに保存する
		productsRepository.save(addProduct);
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
	}

}
