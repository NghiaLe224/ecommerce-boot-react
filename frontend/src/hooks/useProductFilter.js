import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useSearchParams } from "react-router-dom";
import { getDashboardProducts, getProducts } from "../store/product-slice";

const useProductFilter = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();

  useEffect(() => {
    const currentPage = searchParams.get("page")
      ? Number(searchParams.get("page"))
      : 1;
    const sortOrder = searchParams.get("sortOrder") || "asc";
    const category = searchParams.get("category") || undefined;
    const keyword = searchParams.get("keyword") || undefined;

    const queryObject = {
      pageNumber: Math.max(currentPage - 1, 0),
      pageSize: 4,
      sortBy: "price",
      sortOrder,
    };

    if (category) queryObject.category = category;
    if (keyword) queryObject.keyword = keyword;

    console.log("Query Object:", queryObject);

    dispatch(getProducts(queryObject));
  }, [dispatch, searchParams]);
};

export const useDashboardProductFilter = () => {
  const { userResponse } = useSelector((state) => state.auth);
  const isAdmin = userResponse?.roles?.includes("ADMIN");

  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();

  useEffect(() => {
    const currentPage = Number(searchParams.get("page") || 1);
    const sortOrder = searchParams.get("sortOrder") || "asc";
    const sortBy = searchParams.get("sortBy") || "createdAt";
    const category = searchParams.get("category") || undefined;
    const keyword = searchParams.get("keyword") || undefined;

    const params = {
      pageNumber: Math.max(currentPage - 1, 0),
      pageSize: 10,
      sortBy,
      sortOrder,
    };

    if (category) params.category = category;
    if (keyword) params.keyword = keyword;

    dispatch(getDashboardProducts({ isAdmin, params }));
  }, [dispatch, searchParams]);
};

export default useProductFilter;
