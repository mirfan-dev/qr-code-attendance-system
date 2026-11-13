import { useState } from "react";

import { toast } from "react-toastify";

import { NavLink, useNavigate } from "react-router-dom";
import { BsEye, BsEyeSlash } from "react-icons/bs";
import { registerUser } from "../services/user.service";
import { Button, Container, Spinner } from "react-bootstrap";

const SignUp = () => {
  const [data, setData] = useState({
    name: "",
    email: "",
    password: "",
    confirmedPassword: "",
    gender: "",
    about: "",
  });

  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (event, property) => {
    setData({ ...data, [property]: event.target.value });
  };

  const clearData = () => {
    setData({
      name: "",
      email: "",
      password: "",
      confirmedPassword: "",
      gender: "",
      about: "",
    });
  };

  const submitForm = (event) => {
    event.preventDefault();

    if (!data.name || !data.name.trim()) {
      toast.error("Name is required");
      return;
    }
    if (!data.email || !data.email.trim()) {
      toast.error("Email is required");
      return;
    }

    if (!data.password || !data.password.trim()) {
      toast.error("Password is required");
      return;
    }
    if (!data.confirmedPassword || !data.confirmedPassword.trim()) {
      toast.error("ConfirmedPassword is required");
      return;
    }
    if (data.password !== data.confirmedPassword) {
      toast.error("Password and Confirmed password not match");
      return;
    }

    setLoading(true);
    registerUser(data)
      .then((user) => {
        toast.success("User Created Successfully !!");
        clearData();
         localStorage.setItem("qrToken", user.qrCodeToken);
        localStorage.setItem("userId", user.id); 
         navigate("/profile", { state: { token: user?.qrCodeToken } }); 
      })
      .catch((error) => {
        console.log(error);
        toast.error("Error in creating user");
      })
      .finally(() => setLoading(false));
  };

  return (
    <div className="bg-gray-100 flex items-center justify-center min-h-screen px-4">
      <form
        className="bg-white p-8 rounded-lg shadow-md w-full max-w-lg space-y-6"
        onSubmit={submitForm}
      >
        <h2 className="text-2xl font-bold text-center text-gray-600 mb-3">
          Sign Up
        </h2>

        {/* name + email */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            type="text"
            placeholder="Enter Name"
            onChange={(e) => handleChange(e, "name")}
            value={data.name}
            className="border border-blue-800 rounded-md px-4 py-2"
          />
          <input
            type="email"
            placeholder="Enter Email"
            onChange={(e) => handleChange(e, "email")}
            value={data.email}
            className="border border-blue-800 rounded-md px-4 py-2"
          />
        </div>
        {/* Gender */}
        <div className="mb-4">
          <label className="mb-3 block text-sm font-semibold text-gray-700">
            Select Gender
          </label>

          <div className="flex items-center gap-8">
            {/* Male */}
            <label className="flex cursor-pointer items-center gap-3">
              <input
                type="radio"
                name="gender"
                id="gender-male"
                value="male"
                checked={data.gender === "male"}
                onChange={(event) => handleChange(event, "gender")}
                className="h-5 w-5 cursor-pointer accent-blue-600"
              />
              <span className="text-sm text-gray-600">Male</span>
            </label>

            {/* Female */}
            <label className="flex cursor-pointer items-center gap-3">
              <input
                type="radio"
                name="gender"
                id="gender-female"
                value="female"
                checked={data.gender === "female"}
                onChange={(event) => handleChange(event, "gender")}
                className="h-5 w-5 cursor-pointer accent-blue-600"
              />
              <span className="text-sm text-gray-600">Female</span>
            </label>
          </div>
        </div>

        {/* about */}
        <div className="grid grid-cols-1  gap-4">
          <textarea
            rows={5}
            placeholder="Enter Your Message"
            value={data.about}
            onChange={(e) => handleChange(e, "about")}
            className="w-full border border-blue-800 rounded-md px-4 py-2"
          ></textarea>
        </div>

        {/* password */}
        <div className="relative">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="Enter Password"
            value={data.password}
            onChange={(e) => handleChange(e, "password")}
            className="border border-blue-800 rounded-md px-4 py-2 w-full pr-12"
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 bg-transparent border-0 p-0"
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? <BsEyeSlash /> : <BsEye />}
          </button>
        </div>

        <div className="relative">
          <input
            type={showConfirmPassword ? "text" : "password"}
            placeholder="Confirm Password"
            value={data.confirmedPassword}
            onChange={(e) => handleChange(e, "confirmedPassword")}
            className="border border-blue-800 rounded-md px-4 py-2 w-full pr-12"
          />
          <button
            type="button"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
            aria-label={showConfirmPassword ? "Hide password" : "Show password"}
          >
            {showConfirmPassword ? <BsEyeSlash /> : <BsEye />}
          </button>
        </div>

        <Container className="text-center mt-3">
          <p className="text-center">
            Already have an account?{" "}
            <NavLink
              to="/login"
              className="text-primary font-bold no-underline"
            >
              Sign In
            </NavLink>
          </p>
        </Container>

        <Container className="text-center">
          <Button
            type="submit"
            className="text-uppercase"
            variant="success"
            disabled={loading}
          >
            <Spinner
              animation="border"
              size="sm"
              className="me-2"
              hidden={!loading}
            />
            <span hidden={!loading}>Wait...</span>
            <span hidden={loading}>Register</span>
          </Button>
          <Button
            type="button"
            className="ms-3 text-uppercase"
            variant="danger"
            onClick={clearData}
          >
            Reset
          </Button>
        </Container>
      </form>
    </div>
  );
};

export default SignUp;