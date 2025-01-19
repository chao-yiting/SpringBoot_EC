package com.example.demo.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.request.CategoryRequest;

@Service
public class AdminCategoriesService {

	@Autowired //依存性注入
	private CategoryRepository categoryRepository;

	//すべてのカテゴリーを取得し、コントローラに返す
	public List<Category> getAllCategories() { //すべてのカテゴリーをデータベースから取得する
		return categoryRepository.findAll(); //データベース内のすべてのレコードを抽出してリストとして返す
	}

	//新しいカテゴリーをデータベースに保存する
	public void saveCategory(CategoryRequest categoryRequest) {
		Category addCategory = new Category();
		addCategory.setCategoryName(categoryRequest.getCategoryName());

		categoryRepository.save(addCategory);
	}

	// カテゴリーをIDで検索し、受け取った情報で更新する
	public void updateCategory(Long categoryId, CategoryRequest categoryRequest) {
		// カテゴリーIDを使用して、データベース内の既存のカテゴリーを取得
		categoryRepository.findById(categoryId).ifPresent(existingCategory -> {
			existingCategory.setCategoryName(categoryRequest.getCategoryName());
			categoryRepository.save(existingCategory);
		});
	}

	// カテゴリーをIDで削除するメソッド
	public void deleteCategory(Long categoryId) {
		categoryRepository.deleteById(categoryId);
	}
}
