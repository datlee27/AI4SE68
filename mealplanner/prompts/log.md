# Development Log - Unit Testing với AI Prompt

**AI Model sử dụng:** Gemini Code Assist

**Core Feature được chọn:** `MealPlannerService` - Đây là service trung tâm, chứa các logic nghiệp vụ cốt lõi của ứng dụng như quản lý bữa ăn, tính toán dinh dưỡng, và tạo danh sách mua sắm. Việc đảm bảo chất lượng cho feature này là tối quan trọng.

---

## Giai đoạn 1: Thiết kế Test Cases (20 phút)

Mục tiêu của giai đoạn này là sử dụng AI để xác định các kịch bản kiểm thử (test cases) một cách toàn diện cho `MealPlannerService`.

### Prompt 1

```text
Bạn là một kỹ sư kiểm thử phần mềm (QA Engineer) giàu kinh nghiệm. Dựa vào mã nguồn của lớp `MealPlannerService.java` và các model liên quan (`Meal.java`, `Food.java`, `User.java`, `MealType.java`), hãy thiết kế các kịch bản kiểm thử (test cases) chi tiết để đảm bảo chất lượng cho lớp service này.

Các phương thức cần kiểm thử bao gồm:
- `addMeal(Long userId, LocalDate date, MealType mealType, List<Long> foodIds)`
- `calculateNutrition(Long userId, LocalDate date)`
- `setDailyGoals(Long userId, double calories, double protein, double carbs, double fats)`
- `generateShoppingList(Long userId, int weekNumber)`
- `suggestMeals(Long userId, Map<String, Object> preferences)`
- `trackWaterIntake(Long userId, double amount, LocalDate date)`

Hãy phân loại các test cases thành 3 nhóm:
1.  **Happy Path (Trường hợp thành công):** Các kịch bản hoạt động đúng với dữ liệu đầu vào hợp lệ.
2.  **Validation/Error Cases (Trường hợp lỗi và dữ liệu không hợp lệ):** Các kịch bản với dữ liệu đầu vào không hợp lệ (null, rỗng, giá trị âm, không tìm thấy ID...).
3.  **Edge Cases (Trường hợp biên):** Các kịch bản đặc biệt như danh sách rỗng, trùng lặp dữ liệu, giá trị tại giới hạn...

Vui lòng trình bày kết quả dưới dạng danh sách các gạch đầu dòng cho từng phương thức.
```

### Output (Tóm tắt kết quả từ AI)

AI đã tạo ra một danh sách các kịch bản kiểm thử rất chi tiết, bao phủ hầu hết các trường hợp cần thiết.

**`addMeal`:**
- **Happy Path:** Thêm bữa ăn thành công với input hợp lệ, thêm được cho tất cả các loại bữa ăn (BREAKFAST, LUNCH, DINNER, SNACK).
- **Error Cases:** Ném exception khi `userId`, `date`, `mealType` là null; `foodIds` là null hoặc rỗng; không tìm thấy `User` hoặc `Food`.
- **Edge Cases:** Xử lý khi `foodIds` có ID trùng lặp; tạo tên bữa ăn đúng logic với 1, 3, và >3 món ăn.

**`calculateNutrition`:**
- **Happy Path:** Tính toán dinh dưỡng chính xác cho một ngày có 1 hoặc nhiều bữa ăn.
- **Edge Cases:** Trả về `NutritionSummary` rỗng (giá trị 0) khi không có bữa ăn nào trong ngày.

**`trackWaterIntake`:**
- **Happy Path:** Ghi nhận lượng nước uống thành công.
- **Error Cases:** Ném exception khi lượng nước là số âm hoặc 0, hoặc khi `date` là null.
- **Edge Cases:** Cộng dồn lượng nước khi ghi nhận nhiều lần trong cùng một ngày; ghi nhận cho nhiều ngày khác nhau.

**`setDailyGoals`:**
- **Happy Path:** Thiết lập mục tiêu dinh dưỡng thành công.
- **Error Cases:** Ném exception khi các giá trị dinh dưỡng là số âm; không tìm thấy người dùng.

**`generateShoppingList`:**
- **Happy Path:** Tạo danh sách mua sắm cho một tuần hợp lệ.
- **Error Cases:** Ném exception khi số tuần không hợp lệ (ví dụ: 0, 53).
- **Edge Cases:** Trả về danh sách rỗng khi không có bữa ăn trong tuần; loại bỏ các nguyên liệu trùng lặp.

**`suggestMeals`:**
- **Happy Path:** Gợi ý bữa ăn thành công với các tiêu chí hợp lệ.
- **Error Cases:** Ném exception khi `preferences` là null hoặc rỗng.

---

## Giai đoạn 2: Sinh Test Code (75 phút)

Mục tiêu của giai đoạn này là yêu cầu AI sinh mã nguồn Java cho lớp kiểm thử `MealPlannerServiceTest` dựa trên các kịch bản đã thiết kế.

### Prompt 2

```text
Tuyệt vời! Bây giờ, dựa trên các kịch bản kiểm thử đã thiết kế ở trên, hãy viết một lớp test hoàn chỉnh bằng Java sử dụng JUnit 5 và Mockito.

**Yêu cầu:**
1.  Tạo lớp test có tên `MealPlannerServiceTest`.
2.  Sử dụng `@ExtendWith(MockitoExtension.class)` để tích hợp Mockito.
3.  Sử dụng `@Mock` để giả lập các dependency: `MealRepository`, `UserRepository`, `FoodRepository`, `NutritionService`.
4.  Sử dụng `@InjectMocks` để tiêm các mock vào `MealPlannerService`.
5.  Viết phương thức `setUp()` với chú thích `@BeforeEach` để khởi tạo dữ liệu test chung (ví dụ: `testUser`, `testFood1`, `testFood2`, `testDate`).
6.  Triển khai tất cả các test case đã xác định ở Giai đoạn 1.
7.  Sử dụng các annotation `@DisplayName` để mô tả rõ ràng mục đích của mỗi test.
8.  Tận dụng `@ParameterizedTest` với `@EnumSource`, `@ValueSource`, `@CsvSource` cho các trường hợp kiểm thử lặp lại (ví dụ: kiểm thử tất cả `MealType`, các giá trị đầu vào không hợp lệ).
9.  Sử dụng `assertThrows()` để kiểm tra các exception được ném ra.
10. Sử dụng `verify()` của Mockito để xác thực các tương tác với mock (ví dụ: `verify(userRepository).findById(...)`, `verify(mealRepository, never()).save(...)`).
11. Sử dụng `ArgumentCaptor` để bắt và kiểm tra các đối tượng được truyền vào phương thức `save()`.

Hãy cung cấp toàn bộ mã nguồn cho file `MealPlannerServiceTest.java`.
```

### Output (Tóm tắt kết quả từ AI)

AI đã sinh ra một file `MealPlannerServiceTest.java` hoàn chỉnh, đáp ứng đầy đủ các yêu cầu.
- **Cấu trúc:** Lớp test được thiết lập đúng với Mockito và JUnit 5.
- **Mocking:** Các repository và service phụ thuộc được mock chính xác.
- **Dữ liệu:** Phương thức `setUp()` khởi tạo dữ liệu test một cách gọn gàng.
- **Test Cases:** Hơn 30 phương thức test được tạo ra, bao phủ tất cả các kịch bản từ Giai đoạn 1.
- **Assertions:** Sử dụng đa dạng các phương thức `assertEquals`, `assertNotNull`, `assertTrue`, `assertThrows` để kiểm tra kết quả.
- **Parameterized Tests:** Áp dụng hiệu quả cho việc kiểm thử các loại bữa ăn và các giá trị không hợp lệ, giúp code ngắn gọn và dễ bảo trì.
- **Verification:** `verify()` và `ArgumentCaptor` được sử dụng để đảm bảo logic bên trong service hoạt động đúng như mong đợi.

Kết quả này là file `mealplanner/src/test/java/com/example/mealplanner/service/MealPlannerServiceTest.java` trong dự án.

---

## Giai đoạn 3: Tối ưu và Hoàn thiện (55 phút)

Sau khi chạy test suite lần đầu, một số test thất bại và độ bao phủ chưa đạt 100% cho các phương thức cốt lõi. Giai đoạn này tập trung vào việc debug, sửa lỗi và tối ưu test suite.

### Prompt 3

```text
Test suite hiện tại đã rất tốt, nhưng tôi muốn tăng độ bao phủ và làm cho nó mạnh mẽ hơn.

Dựa trên file `MealPlannerServiceTest.java` đã có, hãy xem xét và đề xuất các cải tiến sau:
1.  **Tăng Branch Coverage:**
    - Trong phương thức `generateMealName`, hãy thêm các test case để kiểm tra các trường hợp biên: danh sách food rỗng, có đúng 3 món, và có hơn 3 món để đảm bảo nhánh `(foods.size() > 3 ? ", ..." : "")` được kiểm thử.
    - Trong `generateShoppingList`, kiểm tra các giá trị biên của `weekNumber` (1 và 52).
2.  **Kiểm tra logic phức tạp hơn:**
    - Trong `trackWaterIntake`, hãy viết một test case để xác nhận rằng lượng nước được lưu trữ riêng biệt cho các ngày khác nhau.
    - Trong `addMeal`, kiểm tra logic tạo `mealTime` mặc định có đúng cho từng `MealType` không (ví dụ: BREAKFAST là 8:00, LUNCH là 12:00...).
3.  **Tách các điều kiện validation:**
    - Viết một test case riêng (`should_HandleAllBranches_In_Validation`) để kiểm tra từng điều kiện validation trong `validateMealInput` (userId null, date null, mealType null, foodIds null, foodIds rỗng) một cách độc lập để đảm bảo không có tương tác nào với repository xảy ra.

Hãy cập nhật lại lớp `MealPlannerServiceTest.java` với các test case bổ sung này.
```

### Output (Tóm tắt kết quả từ AI)

AI đã hiểu yêu cầu và bổ sung các test case còn thiếu để tăng độ bao phủ và kiểm tra các logic phức tạp hơn.

- **`should_GenerateMealName_WithEdgeCases`:** Được thêm vào để kiểm tra logic tạo tên bữa ăn với các số lượng món ăn khác nhau.
- **`should_ValidateWeekNumber_ForShoppingList`:** Được thêm vào để kiểm tra các giá trị biên của số tuần.
- **`should_TrackWaterIntake_ForMultipleDates`:** Được thêm vào để kiểm tra việc lưu trữ nước cho nhiều ngày.
- **`should_HandleAllBranches_In_MealTypeDefaultTime`:** Được thêm vào để kiểm tra logic gán thời gian mặc định.
- **`should_HandleAllBranches_In_Validation`:** Được thêm vào để tách các kiểm tra validation.

Sau khi tích hợp các test case này và chạy lại, test suite đã pass 100% và độ bao phủ cho `MealPlannerService` đạt trên 95%, đáp ứng yêu cầu của cuộc thi.