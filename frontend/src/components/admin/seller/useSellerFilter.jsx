import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import { fetchSellers } from "../../../store/seller-slice";

const useSellerFilter = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();

  useEffect(() => {
    const currentPage = searchParams.get("page")
      ? Number(searchParams.get("page"))
      : 1;

    dispatch(fetchSellers({ pageNumber: currentPage - 1 }));
  }, [dispatch, searchParams]);
};

export default useSellerFilter;
