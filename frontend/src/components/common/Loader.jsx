import { ThreeDots } from "react-loader-spinner";

export const Loader = ({text}) => {
  return (
    <div className="flex justify-center items-center w-full h-[450px]">
      <div className="flex flex-col items-center gap-1">
        <ThreeDots
          visible={true}
          height="80"
          width="80"
          color="blue"
          radius="9"
          ariaLabel="three-dots-loading"
          wrapperStyle={{}}
          wrapperClass=""
        />
        <p className="text-slate-800">{text ? text : "Please wait..."}</p>
      </div>
    </div>
  );
};
