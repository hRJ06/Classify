import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../App.css';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

const ViewCourse = () => {
  const [courses, setCourses] = useState([]);
  const [showCreateCourseModal, setShowCreateCourseModal] = useState(false);
  const [newCourseName, setNewCourseName] = useState('');
  const [unenrollCourse,setUnenrollCourse] = useState(null);
  const [unenrollCourseModal,setUnEnrollCourseModal] = useState(false);

  const navigate = useNavigate();
  const role = localStorage.getItem('Role');
  const token = localStorage.getItem('token');
  useEffect(() => {
    const getRandomImage = () => {
      const randomImageURL = `https://source.unsplash.com/400x300/?course`;
      return randomImageURL;
    };

    const getAllCourses = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/course/getCourses', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }); 
        console.log(response)
        const coursesWithImages = response.data.map(course => ({
          ...course,
          imageUrl: getRandomImage()
        }));

        setCourses(coursesWithImages);
      } catch (error) {
        console.error('Error fetching courses:', error);
      }
    };

    getAllCourses();
  }, []);
  const handleCopyCourse = (courseName) => {
    navigator.clipboard.writeText(courseName);
    toast.success(`Course Code Copied`);
  };
  const handleUnEnrollCourse = async(courseId) => {
    const response = await axios.put(`http://localhost:8080/api/v1/course/unenroll/${unenrollCourse}`,null,{
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    if(response?.data?.success) {
      toast.success(response?.data?.result)
      const reload = setTimeout(() => {
        window.location.reload()
      },4000)
    }
    else {
      toast.error(response?.data?.result)
    }
  }

  const handleCreateCourse = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.post(
        'http://localhost:8080/api/v1/course/create',
        { courseName: newCourseName },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      toast.success("Course Created")
    } catch (error) {
      console.error('Error creating course:', error);
    }

    // Close the modal and reset the new course name
    setShowCreateCourseModal(false);
    setNewCourseName('');
  };

  return (
    <div className="container mx-auto mt-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8 font-ubuntu relative">
      {
        role === "INSTRUCTOR" && 
        <div className="absolute top-0 right-0 m-4">
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            onClick={() => setShowCreateCourseModal(true)}
          >
            +
          </button>
        </div>
      }

      {courses.map(course => (
        <div key={course.id} className="bg-white rounded-lg overflow-hidden shadow-md text-center border border-gray-300">
          <img src={course.imageUrl} alt={course.courseName} className="w-full h-48 object-cover object-center" />
          <div className="px-6 py-4">
            <div className="font-bold text-xl mb-2">{course.courseName}</div>
            <div className="flex justify-center space-x-4">
              <button className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600" onClick={() => navigate(`/course/${course.id}`)}>View</button>
              {
                role === "INSTRUCTOR" && 
                <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600" onClick={() => handleCopyCourse(course.code)}>Code</button>
              }
              {
                role === "STUDENT" && 
                <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600" onClick={() => {
                      setUnenrollCourse(course.id)
                      setUnEnrollCourseModal(true)
                    }
                  }
                >
                  UNENROLL
                </button>
              }
            </div>
          </div>
        </div>
      ))}

      {/* Create Course Modal */}
      {showCreateCourseModal && (
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">NEW COURSE</h2>
            <label className="block mb-2">
              Name
              <input
                type="text"
                value={newCourseName}
                onChange={(e) => setNewCourseName(e.target.value)}
                className="border rounded w-full p-2"
              />
            </label>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleCreateCourse}
            >
              CREATE
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={() => setShowCreateCourseModal(false)}
            >
              CANCEL
            </button>
          </div>
        </div>
      )}

      {unenrollCourseModal && (
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">Are You Sure?</h2>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleUnEnrollCourse}
            >
              YES
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={() => setUnEnrollCourseModal(false)}
            >
              NO
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ViewCourse;
