import React from 'react';
import '../App.css';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';

const Home = () => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('Role');
  const handleLogOut = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('Role');
    toast.success("Logged Out")
    const reload = setTimeout(() => {
      window.location.reload();
    },3000)
  }
  return (
    <div className="bg-gray-100 min-h-screen flex items-center justify-center transition-opacity duration-1000 ease-in-out">
      <div className="text-center font-ubuntu md:w-2/3 lg:w-1/2 xl:w-1/3">
        <h1 className="text-4xl font-bold text-blue-600 mb-4 hover:opacity-100 transition-opacity duration-300">Welcome to Classify</h1>
        <p className="text-gray-600 mb-8 hover:opacity-100 transition-opacity duration-300">Transforming Your Learning Experience in the Digital Classroom</p>

        {
          !token && 
          <div className="space-x-4">
            <Link to='/login'><button className="bg-blue-500 text-white px-4 py-2 rounded transition-all duration-300 hover:bg-blue-600">Log In</button></Link>
            <Link to='/register'><button className="bg-green-500 text-white px-4 py-2 rounded transition-all duration-300 hover:bg-green-600">Register</button></Link>
          </div>
        }
        {
          token && 
          <div className="space-x-4">
            <Link to='/getCourses'><button className="bg-blue-500 text-white px-4 py-2 rounded transition-all duration-300 hover:bg-blue-600">View Course</button></Link>
            {
              role === "STUDENT" && (
                <Link to='/enroll'><button className="bg-green-500 text-white px-4 py-2 rounded transition-all duration-300 hover:bg-green-600">Enroll</button></Link>
              )
            }
            <button className="bg-red-500 text-white px-4 py-2 rounded transition-all duration-300 hover:bg-red-600" onClick={handleLogOut}>Log Out</button>
          </div>
        }
      </div>
    </div>
  );
};

export default Home;
