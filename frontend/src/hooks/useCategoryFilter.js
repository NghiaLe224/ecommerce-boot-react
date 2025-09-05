import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import { fetchCategoriesPaging } from "../store/category-slice";

const useCategoryFilter = () => {
  const [searchParams] = useSearchParams(); 
  const dispatch = useDispatch(); 

  useEffect(() => {
    const params = {};

    // Lấy page hiện tại từ query string, mặc định = 1
    const currentPage = searchParams.get("page")
      ? Number(searchParams.get("page"))
      : 1;

    // ⚠️ Sửa "pageNumber" -> "page" để backend nhận đúng
    params.page = currentPage - 1; // backend tính từ 0

    // Có thể truyền thêm size nếu muốn (default backend có rồi)
    params.size = searchParams.get("size") || 8;

    dispatch(fetchCategoriesPaging({ params }));
  }, [dispatch, searchParams]);
};

export default useCategoryFilter;
