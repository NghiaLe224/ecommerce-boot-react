import React, { useState } from "react";
import { MdAddShoppingCart } from "react-icons/md";
import { useDispatch, useSelector } from "react-redux";
import { FaBoxOpen } from "react-icons/fa";
import { DataGrid } from "@mui/x-data-grid";
import AddProductForm from "./AddProductForm";
import toast from "react-hot-toast";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import ProductViewModal from "../../common/productViewModal";
import DeleteModal from "../../common/DeleteModal";
import Modal from "../../common/Modal";
import { Loader } from "../../common/Loader";
import { useDashboardProductFilter } from "../../../hooks/useProductFilter";
import ImageUploadForm from "./ImageUploadForm";
import { adminProductTableColumn } from "../../helper/tableColumn";
import { deleteProduct } from "../../../store/product-slice";

const AdminProducts = () => {
  const {
    items,
    pagination,
    loading: isLoading,
    error: errorMessage,
  } = useSelector((state) => state.products);
  const [currentPage, setCurrentPage] = useState(
    pagination?.pageNumber + 1 || 1
  );

  const dispatch = useDispatch();

  const [selectedProduct, setSelectedProduct] = useState("");
  const [openUpdateModal, setOpenUpdateModal] = useState(false);
  const [openAddModal, setOpenAddModal] = useState(false);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const [openImageUploadModal, setOpenImageUploadModal] = useState(false);

  const [loader, setLoader] = useState(false);

  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const params = new URLSearchParams(searchParams);
  const pathname = useLocation().pathname;

  const { userResponse } = useSelector((state) => state.auth);
  const isAdmin = userResponse && userResponse?.roles?.includes("ADMIN");

  useDashboardProductFilter();

  const tableRecords = items?.map((item) => {
    return {
      id: item.id,
      productName: item.name,
      description: item.description,
      discount: (item.price - item.specialPrice) / item.price,
      image: item.image,
      price: item.price,
      quantity: item.stock,
      specialPrice: item.specialPrice,
    };
  });

  const handleEdit = (product) => {
    setSelectedProduct(product);
    setOpenUpdateModal(true);
  };

  const handleDelete = (product) => {
    setSelectedProduct(product);
    setOpenDeleteModal(true);
  };

  const handleImageUpload = (product) => {
    setSelectedProduct(product);
    setOpenImageUploadModal(true);
  };

  const handleProductView = (product) => {
    setSelectedProduct(product);
    setOpenProductViewModal(true);
  };

  const handlePaginationChange = (paginationModel) => {
    const page = paginationModel.page + 1;
    setCurrentPage(page);
    params.set("page", page.toString());
    navigate(`${pathname}?${params}`);
  };

  const onDeleteHandler = () => {
    setLoader(true);
    dispatch(deleteProduct({ productId: selectedProduct.id, isAdmin }))
      .unwrap()
      .then((res) => {
        toast.success(res.message || "Product deleted successfully");
        setOpenDeleteModal(false);
      })
      .catch((err) => {
        toast.error(err?.message || "Something went wrong");
      })
      .finally(() => {
        setLoader(false);
      });
  };

  const emptyProduct = !items || items?.length === 0;
  return (
    <div>
      <div className="pt-6 pb-10 flex justify-end">
        <button
          onClick={() => setOpenAddModal(true)}
          className="bg-custom-blue hover:bg-blue-800 text-white font-semibold py-2 px-4 flex items-center gap-2 rounded-md shadow-md transition-colors hover:text-slate-300 duration-300"
        >
          <MdAddShoppingCart className="text-xl" />
          Add Product
        </button>
      </div>

      {!emptyProduct && (
        <h1 className="text-slate-800 text-3xl text-center font-bold pb-6 uppercase">
          All Products
        </h1>
      )}
      {isLoading ? (
        <Loader />
      ) : (
        <>
          {emptyProduct ? (
            <div className="flex flex-col items-center justify-center text-gray-600 py-10">
              <FaBoxOpen size={50} className="mb-3" />
              <h2 className="text-2xl font-semibold">
                No products created yet
              </h2>
            </div>
          ) : (
            <div className="max-w-full">
              <DataGrid
                className="w-full"
                rows={tableRecords}
                columns={adminProductTableColumn(
                  handleEdit,
                  handleDelete,
                  handleImageUpload,
                  handleProductView
                )}
                paginationMode="server"
                rowCount={pagination?.totalElements || 0}
                initialState={{
                  pagination: {
                    paginationModel: {
                      pageSize: pagination?.pageSize || 10,
                      page: currentPage - 1,
                    },
                  },
                }}
                onPaginationModelChange={handlePaginationChange}
                disableRowSelectionOnClick
                disableColumnResize
                pageSizeOptions={[pagination?.pageSize || 10]}
                pagination
                paginationOptions={{
                  showFirstButton: true,
                  showLastButton: true,
                  hideNextButton: currentPage === pagination?.totalPages,
                }}
              />
            </div>
          )}
        </>
      )}

      <Modal
        open={openUpdateModal || openAddModal}
        setOpen={openUpdateModal ? setOpenUpdateModal : setOpenAddModal}
        title={openUpdateModal ? "Update Product" : "Add Product"}
      >
        <AddProductForm
          setOpen={openUpdateModal ? setOpenUpdateModal : setOpenAddModal}
          product={selectedProduct}
          update={openUpdateModal}
        />
      </Modal>

      <Modal
        open={openImageUploadModal}
        setOpen={setOpenImageUploadModal}
        title="Add Product Image"
      >
        <ImageUploadForm
          setOpen={setOpenImageUploadModal}
          product={selectedProduct}
        />
      </Modal>

      <DeleteModal
        open={openDeleteModal}
        setOpen={setOpenDeleteModal}
        loader={loader}
        title="Delete Product"
        onDeleteHandler={onDeleteHandler}
      />

      <ProductViewModal
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectedProduct}
      />
    </div>
  );
};

export default AdminProducts;
