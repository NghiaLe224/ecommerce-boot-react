import { FaExclamationTriangle } from "react-icons/fa";
import ProductCard from "../common/ProductCard";
import { useDispatch, useSelector } from "react-redux";
import Filter from "./Filter";
import useProductFilter from "../../hooks/useProductFilter";
import { useEffect } from "react";
import { getCategories } from "../../store/product-slice";
import { Loader } from "../common/Loader";
import { Paginations } from "../common/Paginations";
const Products = () => {
  const dispatch = useDispatch();

  const {
    items: products,
    loading: isLoading,
    error: errMessage,
    categories,
    pagination
  } = useSelector((state) => state.products);

  useEffect(() => {
    dispatch(getCategories());
  }, [dispatch]);

  
  useProductFilter();

  return (
    <div className="lg:px-14 sm:px-8 py-14 2x1:w-[90%] 2x1:mx-auto">
      <Filter categories={categories ? categories : []} />
      {isLoading ? (
        <Loader text={"Products Loading"} />
      ) : errMessage ? (
        <div className="flex justify-center items-center h-[200px]">
          <FaExclamationTriangle className="text-slate-800 text-3xl mr-2" />
          <span className="text-slate-800 text-lg font-medium">
            {errMessage}
          </span>
        </div>
      ) : (
        <div className="min-h-[700px]">
          <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:grid-cols-3 sm:gird-cols-2 gap-y-6 gap-x-6">
            {products &&
              products.map((item, i) => <ProductCard 
              key={i} 
              productId={item.id}
              {...item} />)}
          </div>
          <div className="flex justify-center pt-10">
            <Paginations 
            numberOfPage = {pagination?.totalPages}
            totalProducts = {pagination?.totalElements}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Products;
