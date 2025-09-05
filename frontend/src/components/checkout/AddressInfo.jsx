import React, { useState } from "react";
import Skeleton from "../common/Skeleton";
import { FaAddressBook } from "react-icons/fa";
import AddressInfoModal from "./AddressInfoModal";
import AddAddressForm from "./AddAddressForm";
import { useDispatch, useSelector } from "react-redux";
import AddressList from "./AddressList";
import { DeleteModal } from "./DeleteModal";
import { deleteAddress } from "../../store/address-slice";
import toast from "react-hot-toast";
import { deleteUserAddress } from "../../store/auth-slice";

const AddressInfo = ({ addresses }) => {
  const noAddressExist = !addresses || addresses.length === 0;
  const isLoading = useSelector((state) => state.address.loading);
  const [openAddressModal, setOpenAddressModal] = useState(false);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  const [selectedAddress, setSelectedAddress] = useState();

  const addNewAddressHandler = () => {
    setSelectedAddress("");
    setOpenAddressModal(true);
  };

  const dispatch = useDispatch();
  const deleteAddressHandler = async () => {
    try {
      await dispatch(deleteAddress(selectedAddress?.id)).unwrap();
      setOpenDeleteModal(false); // ✅ ĐÓNG modal bằng tay
      setSelectedAddress(""); // ✅ Reset lại selecte
      dispatch(deleteUserAddress())
      toast.success("Deleted address successfully");
    } catch (err) {
      toast.error(
        typeof err === "string" ? err : err?.message || "Delete address failed"
      );
    }
    //  finally {
    //   setLoader(false);
    // }
  };

  return (
    <div className="pt-4">
      {noAddressExist ? (
        <div className="p-6 rounded-lg max-w-md mx-auto flex flex-col items-center justify-center">
          <FaAddressBook size={50} className="text-gray-500 mb-4" />
          <h1 className="mb-2 text-slate-900 text-center font-semibold text-2xl">
            No address added yet!
          </h1>
          <p className="mb-6 text-slate-800 text-center">
            Please add your address
          </p>

          <button
            className="px-4 py-2 bg-blue-600 text-white font-medium rounded hover:bg-blue-700 transition-all"
            onClick={addNewAddressHandler}
          >
            Add Address
          </button>
        </div>
      ) : (
        <div className="relative p-6 rounded-lg max-w-md mx-auto">
          <h1 className="text-slate-800 text-center font-bold text-2xl">
            Select Address
          </h1>
          {isLoading ? (
            <div className="py-4 px-8">
              <Skeleton />
            </div>
          ) : (
            <>
              <div className="space-y-4 pt-6">
                <AddressList
                  addresses={addresses}
                  setSelectedAddress={setSelectedAddress}
                  setOpenAddressModal={setOpenAddressModal}
                  setOpenDeleteModal={setOpenDeleteModal}
                />
              </div>

              {addresses.length > 0 && (
                <div className="mt-4">
                  <button
                    onClick={addNewAddressHandler}
                    className="px-4 py-2 bg-blue-600 text-white font-medium rounded-sm hover:bg-blue-700 transition-all"
                  >
                    Add More
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      )}

      <AddressInfoModal open={openAddressModal} setOpen={setOpenAddressModal}>
        <AddAddressForm
          address={selectedAddress}
          setOpenAddressModal={setOpenAddressModal}
        />
      </AddressInfoModal>

      <DeleteModal
        open={openDeleteModal}
        loader={isLoading}
        setOpen={setOpenDeleteModal}
        title="Delete Address"
        onDeleteHandler={deleteAddressHandler}
      />
    </div>
  );
};

export default AddressInfo;
