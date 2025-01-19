// ページ読み込み時にセッションストレージから選択した都道府県を取得してセットする
/*window.onload = function() {
	var selectedPrefectureId = sessionStorage.getItem("selectedPrefectureId");
	if (selectedPrefectureId) {
		document.getElementById("prefecture").value = selectedPrefectureId;
		updateShippingInfo(selectedPrefectureId);
	}
}*/
/*/ここなんかする
window.onload = function() {
	// ページ読み込み時に実行される処理

	// 選択した都道府県のIDを取得
	var selectedPrefectureId = document.getElementById("prefecture").value;

	// バリデーションエラーが発生した場合の処理
	if (validationErrorOccurred) {
		// 選択した都道府県を保持する
		updateShippingInfo(selectedPrefectureId);
	} else {
		// 選択した都道府県を初期化する
		document.getElementById("prefecture").value = "";
	}
};
*/

// 都道府県に応じた送料を保持するグローバル変数
var selectedPostage = 0;

/*/ ページが読み込まれたときに実行される処理（文字列になる）
window.onload = function() {
	// ローカルストレージから情報を取得してselectedPostageを更新
	selectedPostage = localStorage.getItem("selectedPostage") || 0;
	// 合計金額を更新
	updateTotalPrice();
}*/


function updateShippingInfo() {
	var selectedPrefectureId = document.getElementById("prefecture").value;

	// サーバーサイドに都道府県IDを送信して、送料とお届け日数を取得する
	fetch("/shipping/info?prefectureId=" + selectedPrefectureId)
		.then(response => response.json())
		.then(data => {
			displayShippingInfo(data);
			selectedPostage = data.postage; // 選択された都道府県に応じた送料を保持
			// 合計金額を更新
			updateTotalPrice();

			// 送料とお届け予定日をローカルストレージに保存（文字列になる）
			//			localStorage.setItem("selectedPostage", selectedPostage);
			//		localStorage.setItem("deliveryDate", data.deliveryDate);

			/*/ 選択した都道府県、送料、お届け予定日をセッションストレージに保存
		  sessionStorage.setItem("selectedPrefectureId", selectedPrefectureId);
		  sessionStorage.setItem("postage", data.postage);
		  sessionStorage.setItem("deliveryDate", data.deliveryDate);*/
		})

		.catch(error => console.error('Error fetching shipping info:', error));
}

function displayShippingInfo(shippingInfo) {
	var shippingCostElement = document.getElementById("shipping-cost");
	var deliveryDaysElement = document.getElementById("delivery-days");

	if (shippingInfo) {
		shippingCostElement.innerText = shippingInfo.postage;
		// deliveryDaysをdate型に変換し、現在の日付に加算して表示
		var deliveryDate = new Date();
		deliveryDate.setDate(deliveryDate.getDate() + shippingInfo.deliveryDate);
		deliveryDaysElement.innerText = (deliveryDate.getMonth() + 1) + "月" + deliveryDate.getDate() + "日";//deliveryDate.getFullYear() + "年" + 
		//deliveryDaysElement.innerText = "お届け日数: " + shippingInfo.deliveryDate + "日";
	} else {
		shippingCostElement.innerText = "";
		deliveryDaysElement.innerText = "";
	}
}

function updateTotalPrice() {
	// Thymeleafで表示された金額を取得し、カンマを取り除いて数値に変換
	var priceWithTaxElement = document.getElementById("price-with-tax");
	var priceWithTaxText = priceWithTaxElement.textContent;
    var priceWithTax = parseInt(priceWithTaxText.replace(/,/g, ''));

	// 選択した都道府県の送料を合計金額に加算
	var totalPrice = priceWithTax + selectedPostage;
	// カンマ区切りにフォーマット
	var formattedTotalPrice = formatNumberWithCommas(totalPrice);


	// 支払い金額をHTMLに反映
	var totalPriceElement = document.getElementById("total-price");
	totalPriceElement.textContent = formattedTotalPrice;
}

function formatNumberWithCommas(number) {
	return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
