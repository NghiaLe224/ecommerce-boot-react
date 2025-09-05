import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Tooltip,
  Button,
} from "@mui/material";
import { useEffect, useState, useCallback } from "react";
import { FiArrowDown, FiArrowUp, FiRefreshCw, FiSearch } from "react-icons/fi";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Filter = ({categories}) => {

  const [searchParams] = useSearchParams();
  const pathname = useLocation().pathname;
  const navigate = useNavigate();

  const [category, setCategory] = useState("all");
  const [sortOrder, setSortOrder] = useState("asc");
  const [searchTerm, setSearchTerm] = useState("");

  // Đồng bộ state ban đầu từ URL
  useEffect(() => {
    const currentCategory = searchParams.get("category") || "all";
    const currentSortOrder = searchParams.get("sortOrder") || "asc";
    const currentSearchTerm = searchParams.get("keyword") || "";

    setCategory(currentCategory);
    setSortOrder(currentSortOrder);
    setSearchTerm(currentSearchTerm);
  }, [searchParams]);

  // Debounce searchTerm
  useEffect(() => {
    const timer = setTimeout(() => {
      const newParams = new URLSearchParams(window.location.search);
      if (searchTerm) {
        newParams.set("keyword", searchTerm);
      } else {
        newParams.delete("keyword");
      }
      newParams.delete("page");
      navigate(`${pathname}?${newParams.toString()}`, { replace: true });
    }, 500);
    return () => clearTimeout(timer);
  }, [searchTerm, navigate, pathname]);

  // Xử lý thay đổi category
  const handleCategoryChange = useCallback((event) => {
    const selectedCategory = event.target.value;
    const newParams = new URLSearchParams(window.location.search);

    if (selectedCategory === "all") {
      newParams.delete("category");
    } else {
      newParams.set("category", selectedCategory);
    }

    newParams.delete("page"); // reset về page đầu tiên
    navigate(`${pathname}?${newParams.toString()}`);
    setCategory(selectedCategory);
  }, [navigate, pathname]);

  // Xử lý sort
  const toggleSortOrder = useCallback(() => {
    const newOrder = sortOrder === "asc" ? "desc" : "asc";
    setSortOrder(newOrder);

    const newParams = new URLSearchParams(window.location.search);
    newParams.set("sortOrder", newOrder);
    newParams.delete("page");
    navigate(`${pathname}?${newParams.toString()}`);
  }, [sortOrder, navigate, pathname]);

  // Clear toàn bộ filter
  const handleClearFilters = useCallback(() => {
    navigate(pathname); // reset URL
    setCategory("all");
    setSortOrder("asc");
    setSearchTerm("");
  }, [navigate, pathname]);

  return (
    <div className="flex lg:flex-row flex-col-reverse lg:justify-between justify-center items-center gap-4">
      {/* Search bar */}
      <div className="relative flex items-center 2xl:w-[450px] sm:w-[420px] w-full">
        <input
          type="text"
          placeholder="Search Products"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border border-gray-400 text-slate-800 rounded-md py-2 pl-10 pr-4 w-full focus:outline-none focus:ring-2 focus:ring-[#1976d2]"
        />
        <FiSearch className="absolute left-3 text-slate-800 size={20}" />
      </div>

      {/* Category, Sort, Clear */}
      <div className="flex lg:flex-row flex-col gap-4 items-center">
        <FormControl variant="outlined" size="small">
          <InputLabel id="category-select-label">Category</InputLabel>
          <Select
            labelId="category-select-label"
            value={category}
            onChange={handleCategoryChange}
            label="Category"
            className="min-w-[120px]"
          >
            <MenuItem value="all">All</MenuItem>
            {categories.map((item) => (
              <MenuItem key={item.categoryId} value={item.categoryName}>
                {item.categoryName}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <Tooltip title={`Sort by price: ${sortOrder}`}>
          <Button
            onClick={toggleSortOrder}
            variant="contained"
            color="primary"
            className="flex items-center gap-2 h-10"
          >
            Sort By
            {sortOrder === "asc" ? <FiArrowUp size={20} /> : <FiArrowDown size={20} />}
          </Button>
        </Tooltip>

        <button
          className="flex items-center gap-2 bg-rose-900 text-white px-3 py-2 rounded-md transition duration-300 ease-in shadow-md focus:outline"
          onClick={handleClearFilters}
        >
          <FiRefreshCw className="font-semibold" size={16} />
          <span className="font-semibold">Clear Filter</span>
        </button>
      </div>
    </div>
  );
};

export default Filter;
