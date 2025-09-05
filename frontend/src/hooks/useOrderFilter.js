import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useSearchParams } from "react-router-dom";
import { getAllUserOrders } from "../store/order-slice";

const useOrderFilter = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();

    const { userResponse } = useSelector((state) => state.auth);
    const isAdmin = userResponse && userResponse?.roles?.includes("ADMIN");

    useEffect(() => {
        const params = new URLSearchParams();

        const currentPage = searchParams.get("page")
            ? Number(searchParams.get("page"))
            : 1;

        params.set("pageNumber", currentPage - 1);

        // const queryString = params.toString();
        // console.log("QUERY STRING", queryString);
        
        dispatch(getAllUserOrders({pageNumber: currentPage -1, isAdmin}));

    }, [dispatch, searchParams]);
};

export default useOrderFilter;