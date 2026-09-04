import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      colors: {
        saffron: "#B93E0A",
        "saffron-dark": "#8A2D06",
        marigold: "#E8A100",
        maroon: "#7B1E3B",
        cream: "#FFF8F1",
        "cream-card": "#FFEFE0",
        ink: "#3A2A20",
        muted: "#6E5A4B",
      },
      fontFamily: {
        sans: ["Segoe UI", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};
export default config;
