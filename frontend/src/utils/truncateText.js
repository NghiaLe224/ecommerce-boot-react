const truncateText = (text, charLimit = 90) => {
  if (!text) return "";
  return text.length > charLimit ? text.slice(0, charLimit) + "..." : text;
};

export default truncateText;