import React, { useState } from 'react';
import axios from 'axios';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const [showPassword, setShowPassword] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Form submitted:', formData);
    try {
      const response = await axios.post('http://localhost:8080/api/v1/user/login', formData);
      console.log(response)
      const token = response?.data?.token
      if(token) {
        toast.success(response?.data?.message)
        localStorage.setItem('token',token)
        localStorage.setItem('Role',response?.data?.role)
        navigate('/')
      }
      else {
        toast.error(response?.data?.message)
      }
    } catch (error) {
      toast.error('Login Failed');
    }
  };

  return (
    <div className="bg-gray-100 min-h-screen flex items-center justify-center font-ubuntu">
      <div className="bg-white p-8 rounded shadow-md md:w-96 lg:w-2/5 xl:w-1/3">
        <h2 className="text-2xl font-bold mb-4">Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="email" className="block text-gray-600 text-sm font-medium mb-2">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border rounded-md focus:outline-none focus:border-blue-500"
            />
          </div>

          <div className="mb-4 relative">
            <label htmlFor="password" className="block text-gray-600 text-sm font-medium mb-2">Password</label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border rounded-md focus:outline-none focus:border-blue-500"
              />
              <span
                className="absolute top-3 right-3 cursor-pointer"
                onClick={handleTogglePassword}
              >
                {showPassword ? <AiOutlineEye /> : <AiOutlineEyeInvisible />}
              </span>
            </div>
          </div>

          <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Login</button>
        </form>
      </div>
    </div>
  );
};

export default Login;
