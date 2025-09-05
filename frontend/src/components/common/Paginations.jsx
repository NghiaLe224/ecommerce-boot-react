import { Pagination } from "@mui/material";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

export const Paginations = ({ numberOfPage = 1 }) => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const pathname = useLocation().pathname;

  // Lấy page hiện tại từ URL, fallback về 1
  const currentPage = Number(searchParams.get("page")) || 1;

  const handlePageChange = (event, value) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set("page", value.toString());

    navigate(`${pathname}?${newParams.toString()}`);
  };

  return (
    <Pagination
      count={numberOfPage}
      page={currentPage}
      onChange={handlePageChange}
      color="primary"
      shape="rounded"
      boundaryCount={2}
      siblingCount={1}
    />
  );
};
