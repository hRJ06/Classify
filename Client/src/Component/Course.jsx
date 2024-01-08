import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AiOutlineMessage } from 'react-icons/ai';
import axios from "axios";
import toast from "react-hot-toast";
import data from '@emoji-mart/data'
import Picker from '@emoji-mart/react'


const Course = () => {
  const [showAddAssignmentModal, setShowAddAssignmentModal] = useState(false);
  const [showAddAnnouncementModal, setShowAnnouncementModal] = useState(false);
  const [newAssignmentData, setNewAssignmentData] = useState({
    assignmentName: "",
    description: "",
    deadline: "",
    fullMarks: "",
    uploadedFiles: []
  });
  
  const [editAssignment, setEditAssignment] = useState(null);
  const [editAssignmentData, setEditAssignmentData] = useState({
    assignmentName: editAssignment ? editAssignment.assignmentName : '',
    deadline: editAssignment ? editAssignment.deadline : '',
    description: editAssignment ? editAssignment.description : '',
    fullMarks: editAssignment ? editAssignment.fullMarks : '',
  })
  const [editCommentData, setEditCommentData] = useState({
    comment: ''
  })
  const [newAnnouncementData, setNewAnnouncementData] = useState({
    name: "",
    content: "",
    uploadedFiles: [] 
  })
  const [newSubmissionData, setNewSubmissionData] = useState({
    uploadedFiles: []
  })

  const [newFileAssignmentData, setNewFileAssignmentData] = useState({
    uploadedFiles: []
  })

  const { courseId } = useParams();
  const [activeTab, setActiveTab] = useState("assignments");
  const [assignments, setAssignments] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [discussionMessages, setDiscussionMessages] = useState([]);

  const token = localStorage.getItem("token");
  const [expandedAssignmentId, setExpandedAssignmentId] = useState(null);
  const [selectedAssignmentId, setSelectedAssignmentId] = useState(null);
  const [editingSubmissionId, setEditingSubmissionId] = useState(null);
  const [newMarks, setNewMarks] = useState("");
  const [course,setCourse] = useState('');
  const [enrolledUsers, setEnrolledUsers] = useState([])
  const [showDeleteFileModal, setShowDeleteFileModal] = useState(false);
  const [fileToDelete, setFileToDelete] = useState(null);
  const role = localStorage.getItem('Role');
  const [removeSubmissionModal, setRemoveSubmissionModal] = useState(false);
  const [addSubmissionModal, setAddSubmissionModal] = useState(false);
  const [addSubmissionAssignment, setAddSubmissionAssignment] = useState(null);
  const [addFileAssignmentModal, setAddFileAssignmentModal] = useState(false);
  const [addFileAssignment, setAddFileAssignment] = useState(null);
  const [editCommentSubmissionId, setEditCommentSubmissionId] = useState(null);
  const [privateChat,setPrivateChat] = useState(null);
  const [newMessage, setNewMessage] = useState('');
  const [chatAssignment, setChatAssignment] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const usersPerPage = 3;

  const indexOfLastUser = currentPage * usersPerPage;
  const indexOfFirstUser = indexOfLastUser - usersPerPage;
  const currentUsers = enrolledUsers.slice(indexOfFirstUser, indexOfLastUser);

  const handleNextPage = () => {
    setCurrentPage(currentPage + 1);
  };

  const handlePrevPage = () => {
    setCurrentPage(currentPage - 1);
  };
  const isNextButtonVisible = indexOfLastUser < enrolledUsers.length;
  const generateAvatarUrl = (seed) => {
    const baseUrl = 'https://ui-avatars.com/api/?name=';
    return `${baseUrl}${seed}`;
  };
  const handleSendMessage = async(param) => {
    if(newMessage.length <= 0) {
      toast.error('Please Enter A Message')
      return;
    }
    if(param !== "discussion") {
      toast.loading()
      let response = await axios.post(`http://localhost:8080/api/v1/chat/add-message/${privateChat.id}`,{content: newMessage}, {
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      response = await axios.get(`http://localhost:8080/api/v1/chat/get-message/${privateChat.id}`,{
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      setPrivateChat({
        ...privateChat,
        messageList: response?.data?.messageList
      });
      toast.dismiss()
    }
    else {
      toast.loading()
      let response = await axios.post(`http://localhost:8080/api/v1/course/add-discussion-message/${courseId}`, {content: newMessage}, {
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      response = await axios.get(`http://localhost:8080/api/v1/course/get-discussion-message/${courseId}`,{
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      setDiscussionMessages(response?.data?.discussionMessageList);
      toast.dismiss()
    }
    setNewMessage('');
    toast.dismiss()
  };
  const handleEditAssignment = async() => {
    const response = await axios.put(`http://localhost:8080/api/v1/assignment/editAssignment/${editAssignment.id}`,editAssignmentData,{
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    if(response?.data?.success) {
      toast.success(response?.data?.message)
    }
    else {
      toast.error(response?.data?.message)
    }
    if(response?.data?.success) {
      const reload = setTimeout(() => {
        window.location.reload()
      },3000)
    }
  }
  const handleChat = async(param) => {
    if(role === "STUDENT") {
      toast.loading()
      const response = await axios.post(`http://localhost:8080/api/v1/assignment/create-chat/${param.id}`, null, {
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      toast.dismiss()
      console.log(response)
      setPrivateChat(response?.data);
    }
    else {
      toast.loading();
      const formData = new FormData()
      formData.append('StudentId',param);
      const response = await axios.post(`http://localhost:8080/api/v1/assignment/create-chat/${chatAssignment}`, formData, {
        headers: {
          Authorization: 'Bearer ' + token
        }
      })
      toast.dismiss()
      setChatAssignment(null);
      setPrivateChat(response?.data);
    }
  }
  const handleAddFileAssignment = async() => {
    if(newFileAssignmentData.uploadedFiles.length <= 0) {
      toast.error("Please Attach A File")
      return;
    }
    const filesArray = Array.from(newFileAssignmentData.uploadedFiles)
    const formData = new FormData();
    for(let i = 0; i < filesArray.length; i++) {
      formData.append('files', filesArray[i]);
    }
    toast.loading("Adding File(s)")
    const response = await axios.put(`http://localhost:8080/api/v1/file/add/assignment/${addFileAssignment}`,formData, {
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    toast.dismiss()
    if(response?.data?.success) {
      toast.success(response?.data?.result)
    }
    else {
      toast.error(response?.data?.result)
    }
    setNewFileAssignmentData({
      ...newFileAssignmentData,
      uploadedFiles: []
    })
    setAddFileAssignment(null);
    if(response?.data?.success) {
      const result = setTimeout(() => {
        window.location.reload()
      },3000)
    }
  }

  const handleAddSubmissionModal = (assignment) => {
    const hasSubmission = assignment.submissions.length > 0;
    setAddSubmissionAssignment(assignment.id)
    if(hasSubmission) {
      toast.error("Please Remove Your Current Submission");
      return;
    }
    setAddSubmissionModal(true);
  }
  const handleAddSubmission = async() => {
    if(newSubmissionData.uploadedFiles.length <= 0) {
      toast.error("Please Attach A File");
      return;
    }
    const formData = new FormData();
    const filesArray = Array.from(newSubmissionData.uploadedFiles);
    for(let i = 0; i<filesArray.length; i++) {
      formData.append('files',filesArray[i]);
    }
    toast.loading('Adding Your Submission');
    const response = await axios.post(`http://localhost:8080/api/v1/assignment/submitSubmission/${addSubmissionAssignment}`,formData,{
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    toast.dismiss()
    if(response?.data?.success) {
      toast.success(response?.data?.message);
    }
    else {
      toast.error(response?.data?.message);
    }
    setNewSubmissionData({
      ...newSubmissionData,
      uploadedFiles: []
    })
    setAddSubmissionModal(false);
    if(response?.data?.success) {
      const reload = setTimeout(() => {
        window.location.reload();
      },4000)
    }
  }
  const handleRemoveSubmission = () => {
    setRemoveSubmissionModal(true);
  }
  const handleCancelRemoveSubmission = () => {
    setRemoveSubmissionModal(false);
  }
  const handleDeleteSubmission = async() => {
    toast.loading('Removing Submission')
    const response = await axios.put(`http://localhost:8080/api/v1/assignment/removeSubmission/${selectedAssignmentId}`,{},{
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    toast.dismiss()
    setRemoveSubmissionModal(false);
    if(response?.data?.success) {
      toast.success(response?.data?.message)
    }
    else {
      toast.error(response?.data?.message)
    }
    const reload = setTimeout(() => {
      window.location.reload()
    },4000)
  }
  const handleOpenAddAnnouncementModal = () => {
    setShowAnnouncementModal(true);
  }
  const handleOpenAddAssignmentModal = () => {
    setShowAddAssignmentModal(true);
  };

  const handleCloseAddAnnouncementModal = () => {
    setShowAnnouncementModal(false);
    setNewAnnouncementData({
      name: "",
      content: "",
      uploadedFiles: [] 
    })
  }
  const handleCloseAddAssignmentModal = () => {
    setShowAddAssignmentModal(false);
    setNewAssignmentData({
      assignmentName: "",
      description: "",
      deadline: "",
      fullMarks: "",
      uploadedFiles: []
    });
  };
  const toggleDetails = (assignmentId) => {
    setExpandedAssignmentId((prevId) =>
      prevId === assignmentId ? null : assignmentId
    );
  };

  const handleViewSubmissions = (assignmentId) => {
    setSelectedAssignmentId(assignmentId);
  };

  const handleEditMarks = async (submissionId) => {
    setEditingSubmissionId(submissionId);
  };
  const getAvatarInitials = (firstName, lastName) => {
    const firstInitial = firstName ? firstName.charAt(0) : '';
    const lastInitial = lastName ? lastName.charAt(0) : '';
    return firstInitial + lastInitial;
  };
  const handleEditComment = async () => {
    if(editCommentData.comment === editCommentSubmissionId.comment) {
      toast.error('Please Edit')
      return;
    }
    console.log(typeof(editCommentData));
    const response = await axios.put(`http://localhost:8080/api/v1/submission/addComment/${editCommentSubmissionId.id}`,editCommentData,{
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    if(response?.data?.success) {
      toast.success('Comment Added Successfully');
      setEditCommentSubmissionId(null);
    }
    else {
      toast.error(response?.data?.message)
    }
  }
  const handleSaveMarks = async () => {
    const response = await axios.post(
      `http://localhost:8080/api/v1/submission/editMarks/${editingSubmissionId}`,
      { marks: String(newMarks) },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
    if (response?.data?.success) {
      toast.success(response?.data?.message);
      const reload = setTimeout(() => {
        window.location.reload();
      }, 4000);
    } else {
      toast.error(response?.data?.message);
    }
    setEditingSubmissionId(null);
    setNewMarks("");
  };
  // Function to generate a random image URL for the banner
  const getRandomBannerImage = () => {
    return `https://source.unsplash.com/1600x400/?classroom`;
  };
  const handleCreateAssignment = async () => {
    const formData = new FormData();
    formData.append('name',newAssignmentData.assignmentName)
    formData.append('description',newAssignmentData.description);
    formData.append('deadline',newAssignmentData.deadline);
    formData.append('fullMarks',newAssignmentData.fullMarks);
    const filesArray = Array.from(newAssignmentData.uploadedFiles);
    for(let i = 0; i < filesArray.length; i++) {
      formData.append('files', filesArray[i]);
    }
    toast.loading("Creating Assignment", {autoClose:true});
    const response = await axios.post(
        `http://localhost:8080/api/v1/course/createAssignment/${courseId}`,
        formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: 'Bearer ' + token
            },
        }
    );
    toast.dismiss()
    if(response?.data?.success) {
        toast.success(response?.data?.result);
    }
    else {
        toast.error(response?.data?.result);
    }
    console.log(response)
  };

  const handleCreateAnnouncement = async() => {
    const formData = new FormData();
    formData.append('name',newAnnouncementData.name);
    formData.append('content',newAnnouncementData.content);
    const filesArray = Array.from(newAnnouncementData.uploadedFiles);
    for(let i = 0; i < filesArray.length; i++) {
      formData.append('files',filesArray[i]);
    }
    toast.loading("Creating Announcement", {autoClose: true});
    const response = await axios.post(
      `http://localhost:8080/api/v1/course/createAnnouncement/${courseId}`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: 'Bearer ' + token
        }
      }
    )
    toast.dismiss()
    if(response?.data?.success) {
      toast.success(response?.data?.result);
    }
    else {
        toast.error(response?.data?.result);
    }
  }
  const handleDeleteFile = async(fileId) => {
    setFileToDelete(fileId);
    setShowDeleteFileModal(true);
  }
  const handleConfirmDeleteFile = async() => {
    const response = await axios.delete(`http://localhost:8080/api/v1/file/remove/${fileToDelete}`, {
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    console.log(response)
    if(response?.data?.success) {
      toast.success(response?.data?.result);
      const reload = setTimeout(() => {
        window.location.reload()
      },4000)
    }
    else {
      toast.error(response?.data?.result);
    }
    setFileToDelete(null)
    setShowDeleteFileModal(false);
  }
  const handleCancelDeleteFile = () => {
    setShowDeleteFileModal(false);
    setFileToDelete(null);
  };
  useEffect(() => {
    const getAssignments = async () => {
      const response = await axios.get(
        `http://localhost:8080/api/v1/course/getAssignment/${courseId}`,
        {
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      );
      setAssignments(response?.data?.assignmentList);
    };

    const getAnnouncements = async () => {
      const response = await axios.get(
        `http://localhost:8080/api/v1/course/getAnnouncement/${courseId}`,
        {
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      );
      console.log(response)
      setAnnouncements(response?.data?.announcements);
    };

    const getCourseDetails = async() => {
      const response = await axios.get(`http://localhost:8080/api/v1/course/${courseId}`, {
        headers: {
          Authorization: "Bearer " + token,
        }
      })
      setEnrolledUsers(response?.data?.enrolledUsers)
      setCourse(response?.data?.courseName)
    }
    getCourseDetails();
    getAssignments();
    getAnnouncements();
  }, [courseId, token]);
  useEffect(() => {
    setEditAssignmentData({
      assignmentName: editAssignment ? editAssignment.assignmentName : '',
      deadline: editAssignment ? editAssignment.deadline : '',
      description: editAssignment ? editAssignment.description : '',
      fullMarks: editAssignment ? editAssignment.fullMarks : '',
    });
  }, [editAssignment]);
  useEffect(() => {
    const getDiscussionMessages = async() => {
      const response = await axios.get(`http://localhost:8080/api/v1/course/get-discussion-message/${courseId}`, {
        headers: {
          Authorization: 'Bearer ' + token
        }
      })

      setDiscussionMessages(response?.data?.discussionMessageList);
    }
    getDiscussionMessages();
  },[])

  return (
    <div className="font-ubuntu flex flex-col">
      {/* Banner */}
      <div className="relative h-40 md:h-60 lg:h-80 overflow-hidden">
        <img
          src={getRandomBannerImage()}
          alt="Course Banner"
          className="w-full h-full object-cover object-center"
        />
        <div className="absolute inset-0 bg-gradient-to-b from-transparent to-black"></div>
        <div className="absolute inset-x-0 bottom-0 p-4 text-white">
          <h2 className="text-2xl font-bold opacity-75">{course}</h2>
        </div>
      </div>

      {/* Tabs */}
      <div className="container mx-auto mt-8">
        <div className="flex space-x-4">
          <button
            className={`px-4 py-2 font-medium ${
              activeTab === "assignments"
                ? "bg-blue-500 text-white"
                : "bg-gray-300 text-gray-700"
            }`}
            onClick={() => setActiveTab("assignments")}
          >
            ASSIGNMENT
          </button>
          {/* + Button to add assignment */}
          {
            role === "INSTRUCTOR" && 
            <button
              className="px-4 py-2 font-medium bg-green-500 text-white hover:bg-green-600"
              onClick={handleOpenAddAssignmentModal}
            >
              +
            </button>
          }
          <button
            className={`px-4 py-2 font-medium ${
              activeTab === "announcements"
                ? "bg-blue-500 text-white"
                : "bg-gray-300 text-gray-700"
            }`}
            onClick={() => setActiveTab("announcements")}
          >
            ANNOUNCEMENT
          </button>
          {/* + Button to add announcement */}
          {
            role === "INSTRUCTOR" && 
            <button
              className="px-4 py-2 font-medium bg-green-500 text-white hover:bg-green-600"
              onClick={handleOpenAddAnnouncementModal}
            >
              +
            </button>
          }
          <button
            className={`px-4 py-2 font-medium ${
              activeTab === "discussion"
                ? "bg-blue-500 text-white"
                : "bg-gray-300 text-gray-700"
            }`}
            onClick={() => setActiveTab("discussion")}
          >
            DISCUSSION
          </button>
          <button
            className={`px-4 py-2 font-medium ${
              activeTab === "students"
                ? "bg-blue-500 text-white"
                : "bg-gray-300 text-gray-700"
            }`}
            onClick={() => setActiveTab("students")}
          >
            STUDENT
          </button>
        </div>

        {/* Tab Content */}
        <div className="mt-4 p-4 border rounded bg-white">
          {activeTab === "assignments" && (
            <div>
              {/* Add content for Assignments tab */}
              {assignments.map((assignment) => (
                <div key={assignment.id} className="mb-4">
                  <div
                    className="flex justify-between items-center cursor-pointer"
                  >
                    <h3 className="text-xl font-bold">
                      {assignment.assignmentName}
                    </h3>
                    <div className="flex items-center">
                    {/* Edit Button */}
                    {
                      role === "INSTRUCTOR" && 
                      <button
                        className="ml-2 px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                        onClick={() => setEditAssignment(assignment)}
                      >
                        Edit
                      </button>
                    }
                    {/* Expand/Collapse Button */}
                    <span onClick={() => toggleDetails(assignment.id)}>
                      {expandedAssignmentId === assignment.id ? "▲" : "▼"}
                    </span>
                    </div>
                  </div>
                  {expandedAssignmentId === assignment.id && (
                    <div className="mt-2">
                      <p className="text-gray-700 mb-2 font-semibold">
                        Description: {assignment.description}
                      </p>
                      <p className="text-gray-700 mb-2 font-semibold">
                        Deadline:{" "}
                        {new Date(assignment.deadline).toLocaleString()}
                      </p>
                      <p className="text-gray-700 mb-2 font-semibold">
                        Marks: {assignment.fullMarks}
                      </p>
                      {assignment.file && assignment.file.length > 0 && (
                        <div className="mt-2">
                          <p className="text-gray-700 mb-1 font-semibold">
                            Files:
                          </p>
                          <ul className="list-disc ml-4">
                            {assignment.file.map((file) => (
                              <li key={file.id} className="mb-2">
                                <a
                                  href={file.filePath}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  className="text-blue-500 hover:underline"
                                >
                                  {file.fileName}
                                </a>
                                {
                                  role === "INSTRUCTOR" && 
                                  <button
                                    className="ml-2 px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                    onClick={() => handleDeleteFile(file.id)}
                                  >
                                    Delete
                                  </button>
                                }
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}
                      {/* Submission Button */}
                      <button
                        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                        onClick={() => handleViewSubmissions(assignment.id)}
                      >
                        VIEW SUBMISSION
                      </button>
                      {
                        role === "STUDENT" && (
                        <button
                          className="flex-shrink-0 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 ml-2"
                          onClick = {() => handleAddSubmissionModal(assignment)}
                        >
                          ADD SUBMISSION
                        </button>
                      )}
                      {
                        role === "INSTRUCTOR" && (
                        <button
                          className="flex-shrink-0 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 ml-2"
                          onClick = {() => {
                            setAddFileAssignmentModal(true)
                            setAddFileAssignment(assignment.id);
                          }}
                        >
                          ADD FILE
                        </button>
                      )}
                      <button
                        className="flex-shrink-0 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 ml-2"
                      >
                        
                        <div className="flex" onClick={() => role === "STUDENT" ? handleChat(assignment) : setChatAssignment(assignment.id)}>
                          CHAT <AiOutlineMessage className="ml-2" size={24} />
                        </div>
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}

          {activeTab === "announcements" && (
            <div>
              {/* Add content for Announcements tab */}
              {announcements.map((announcement) => (
                <div key={announcement.id} className="mb-4">
                  <h3 className="text-xl font-bold">{announcement.name}</h3>
                  <p className="text-gray-700 mb-2">{announcement.content}</p>
                  {announcement.files && announcement.files.length > 0 && (
                    <div className="mt-2">
                      <p className="text-gray-700 mb-1 font-semibold">Files:</p>
                      <ul className="list-disc ml-4">
                        {announcement.files.map((file) => (
                          <li key={file.id}>
                            <a
                              href={file.filePath}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-500 hover:underline"
                            >
                              {file.fileName}
                            </a>
                            {
                              role === "INSTRUCTOR" &&
                              <button
                                className="ml-2 px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                onClick={() => handleDeleteFile(file.id)}
                              >
                                Delete
                              </button>
                            }
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}

        {
          activeTab === "discussion" && (
            <>
              {discussionMessages.map((message, index) => (
                <div key={index} className="flex mb-2">
                  <div className="flex items-center">
                    <img
                      src={generateAvatarUrl(`${message.sender.firstName}+${message.sender.lastName}`)}
                      alt="Avatar"
                      className="w-8 h-8 rounded-full"
                    />
                  </div>
                  <div className="flex flex-col ml-2">
                    <span className="text-sm font-semibold">
                      {`${message.sender.firstName} ${message.sender.lastName}`}
                      {/* Add role indication if needed */}
                    </span>
                    <span>{message.content}</span>
                  </div>
                </div>
              ))}
              <div className="flex items-center mt-4">
                <input
                  type="text"
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  className="flex-grow border rounded-l p-2 w-full"
                  placeholder="Type A Message..."
                />
                <button
                  className="px-4 py-2 bg-blue-500 text-white rounded-r hover:bg-blue-600 ml-2"
                  onClick={() => handleSendMessage('discussion')}
                >
                  SEND
                </button>
              </div>
            </>
          )
        }

          {/* Display enrolled users directly in the tab content */}
          {activeTab === "students" && (
            <div className="grid grid-cols-3 gap-4">
              <div className="p-4 bg-gray-200 text-center font-semibold">No</div>
              <div className="p-4 bg-gray-200 text-center font-semibold">Name</div>
              <div className="p-4 bg-gray-200 text-center font-semibold">Email</div>

              {enrolledUsers.map((user, index) => (
                <React.Fragment key={user.id}>
                  <div className="p-4 text-center">{index + 1}</div>
                  <div className="p-4 text-center">{user.firstName} {user.lastName}</div>
                  <div className="p-4 text-center">{user.email}</div>
                  {/* Add more cells with user information */}
                </React.Fragment>
              ))}
            </div>
          )}

          {/* Submissions Modal */}
          {selectedAssignmentId && (
            <div className="fixed inset-0 flex items-center justify-center">
              <div className="bg-black opacity-75 fixed inset-0"></div>
              <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">SUBMISSION</h2>
                <table className="w-full">
                  <thead>
                    <tr>
                      <th className="p-4">Name</th>
                      <th className="p-4">Email</th>
                      <th className="p-4">Submission Date</th>
                      <th className="p-4">Late Status</th>
                      <th className="p-4">Files</th>
                      <th className="p-4">Comment</th>
                      {
                        role === "INSTRUCTOR" &&
                        <th className="p-4"></th>
                      }
                      <th className="p-4">Marks</th>
                      <th className="p-4"></th>
                    </tr>
                  </thead>
                  <tbody>
                    {assignments
                      .find(
                        (assignment) => assignment.id === selectedAssignmentId
                      )
                      ?.submissions.map((submission) => (
                        <tr key={submission.id}>
                          <td className="font-semibold p-4">
                            {submission.user.firstName}{" "}
                            {submission.user.lastName}
                          </td>
                          <td className="font-semibold p-4">
                            {submission.user.email}
                          </td>
                          <td className="p-4">
                            {new Date(
                              submission.submissionDateTime
                            ).toLocaleString()}
                          </td>
                          <td className="p-4">
                            {submission.lateStatus ? "Late" : "On Time"}
                          </td>
                          <td className="p-4">
                            <ul className="list-disc ml-4">
                              {submission.file.map((file) => (
                                <li key={file.id}>
                                  <a
                                    href={file.filePath}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-500 hover:underline"
                                  >
                                    {file.fileName}
                                  </a>
                                </li>
                              ))}
                            </ul>
                          </td>
                          
                          <td className="p-4 relative">
                            {submission.comment === null ? (
                              <span className="text-gray-500">N/A</span>
                            ) : (
                              <>
                                {submission.comment}
                              </>
                            )}
                          </td>
                          {
                            role === "INSTRUCTOR" && 
                            <td>
                            <button
                              className="ml-2 px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                              onClick={() => {
                                  setEditCommentSubmissionId(submission)
                                  setEditCommentData({
                                    comment: submission.comment
                                  })
                                }
                              }
                            >
                              Edit
                            </button>
                          </td>
                          }
                          <td className="p-4 relative">
                            {submission.marks === null ? (
                              <span className="text-gray-500">N/A</span>
                            ) : (
                              <>
                                {submission.marks} /{" "}
                                {
                                  assignments.find(
                                    (a) => a.id === selectedAssignmentId
                                  )?.fullMarks
                                }
                              </>
                            )}
                          </td>
                          <td>
                          {
                              role === "INSTRUCTOR" && 
                              <button
                                className="ml-2 px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                                onClick={() => handleEditMarks(submission.id)}
                              >
                                Edit
                              </button>
                            }
                            {
                              role === "STUDENT" && 
                              <button
                                className="ml-2 px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                onClick={() => handleRemoveSubmission(selectedAssignmentId)}
                              >
                                Delete
                              </button>
                            }
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </table>
                <button
                  className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                  onClick={() => setSelectedAssignmentId(null)}
                >
                  Close
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
      {/* Marks Edit Modal */}
      {editingSubmissionId && (
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">EDIT MARK</h2>
            <label className="block mb-2">
              Mark
              <input
                type="text"
                value={newMarks}
                onChange={(e) => setNewMarks(e.target.value)}
                className="border rounded w-full p-2"
              />
            </label>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleSaveMarks}
            >
              Save
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={() => setEditingSubmissionId(null)}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
      {
        editCommentSubmissionId && (
          <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">EDIT COMMENT</h2>
            <label className="block mb-2">
              Comment
              <input
                type="text"
                value={editCommentData.comment}
                onChange={(e) => setEditCommentData({
                  ...editCommentData,
                  comment: e.target.value
                })}
                className="border rounded w-full p-2"
              />
            </label>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleEditComment}
            >
              Save
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={() => setEditCommentSubmissionId(null)}
            >
              Cancel
            </button>
          </div>
        </div>
        )
      }
      {/* Add Assignment Button */}

      {/* Add Assignment Modal */}
      {showAddAssignmentModal && (
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">ADD ASSIGNMENT</h2>
            <label className="block mb-2">
              NAME
              <input
                type="text"
                value={editAssignment.assignmentName}
                onChange={(e) =>
                  setNewAssignmentData({
                    ...newAssignmentData,
                    assignmentName: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              DESCRIPTION
              <textarea
                value={newAssignmentData.description}
                onChange={(e) =>
                  setNewAssignmentData({
                    ...newAssignmentData,
                    description: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              DEADLINE
              <input
                type="datetime-local"
                value={newAssignmentData.deadline}
                onChange={(e) =>
                  setNewAssignmentData({
                    ...newAssignmentData,
                    deadline: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              FULL MARK
              <input
                type="number"
                value={newAssignmentData.fullMarks}
                onChange={(e) =>
                  setNewAssignmentData({
                    ...newAssignmentData,
                    fullMarks: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              UPLOAD FILE
              <input
                type="file"
                multiple
                onChange={(e) =>
                  setNewAssignmentData({
                    ...newAssignmentData,
                    uploadedFiles: e.target.files,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleCreateAssignment}
            >
              CREATE ASSIGNMENT
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={handleCloseAddAssignmentModal}
            >
              CANCEL
            </button>
          </div>
        </div>
      )}

      {/* Add Announcement Modal */}
      {showAddAnnouncementModal && (
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10">
            <h2 className="text-2xl font-bold mb-4">ADD ANNOUNCEMENT</h2>
            <label className="block mb-2">
              NAME
              <input
                type="text"
                value={newAnnouncementData.name}
                onChange={(e) =>
                  setNewAnnouncementData({
                    ...newAnnouncementData,
                    name: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              DESCRIPTION
              <textarea
                value={newAnnouncementData.content}
                onChange={(e) =>
                  setNewAnnouncementData({
                    ...newAnnouncementData,
                    content: e.target.value,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <label className="block mb-2">
              UPLOAD FILE
              <input
                type="file"
                multiple
                onChange={(e) =>
                  setNewAnnouncementData({
                    ...newAnnouncementData,
                    uploadedFiles: e.target.files,
                  })
                }
                className="border rounded w-full p-2"
              />
            </label>
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={handleCreateAnnouncement}
            >
              CREATE ANNOUNCEMENT
            </button>
            <button
              className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              onClick={handleCloseAddAnnouncementModal}
            >
              CANCEL
            </button>
          </div>
        </div>
      )}
      {showDeleteFileModal && (
        <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">DELETE FILE</h2>
                <p>Are you sure you want to delete this file?</p>
                <button
                    className="mt-4 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                    onClick={handleConfirmDeleteFile}
                >
                    Yes
                </button>
                <button
                    className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                    onClick={handleCancelDeleteFile}
                >
                    No
                </button>
            </div>
        </div>
      )} 
      {removeSubmissionModal && (
        <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">REMOVE SUBMISSION</h2>
                <p>Are you sure you want to remove your Submission?</p>
                <button
                    className="mt-4 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                    onClick={handleDeleteSubmission}
                >
                    Yes
                </button>
                <button
                    className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                    onClick={handleCancelRemoveSubmission}
                >
                    No
                </button>
            </div>
        </div>
      )} 
      {addSubmissionModal && (
        <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">ADD SUBMISSION</h2>
                <label className="block mb-2">
                  UPLOAD FILE
                  <input
                    type="file"
                    multiple
                    onChange={(e) =>
                      setNewSubmissionData({
                        ...newSubmissionData,
                        uploadedFiles: e.target.files,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <button
                  className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                  onClick={handleAddSubmission}
                >
                  ADD SUBMISSION
                </button>
                <button
                  className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                  onClick={() => setAddSubmissionModal(false)}
                >
                  CANCEL
                </button>
            </div>
        </div>
      )} 
      {addFileAssignmentModal && (
        <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">ADD FILE</h2>
                <label className="block mb-2">
                  UPLOAD FILE
                  <input
                    type="file"
                    multiple
                    onChange={(e) =>
                      setNewFileAssignmentData({
                        ...newFileAssignmentData,
                        uploadedFiles: e.target.files,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <button
                  className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                  onClick={handleAddFileAssignment}
                >
                  ADD FILE
                </button>
                <button
                  className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                  onClick={() => setAddFileAssignmentModal(false)}
                >
                  CANCEL
                </button>
            </div>
        </div>
      )} 
      {editAssignment && (
        <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
                <h2 className="text-2xl font-bold mb-4">EDIT ASSIGNMENT</h2>
                <label className="block mb-2">
                  Name
                  <input
                    type="text"
                    value={editAssignmentData.assignmentName}
                    onChange={(e) =>
                      setEditAssignmentData({
                        ...editAssignmentData,
                        assignmentName: e.target.value,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <label className="block mb-2">
                  Description
                  <input
                    type="text"
                    value={editAssignmentData.description}
                    onChange={(e) =>
                      setEditAssignmentData({
                        ...editAssignmentData,
                        description: e.target.value,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <label className="block mb-2">
                  Deadline
                  <input
                    type="datetime-local"
                    value={editAssignmentData.deadline}
                    onChange={(e) =>
                      setEditAssignmentData({
                        ...editAssignmentData,
                        deadline: e.target.value,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <label className="block mb-2">
                  Marks
                  <input
                    type="number"
                    value={editAssignmentData.fullMarks}
                    onChange={(e) =>
                      setEditAssignmentData({
                        ...editAssignmentData,
                        marks: e.target.value,
                      })
                    }
                    className="border rounded w-full p-2"
                  />
                </label>
                <button
                  className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                  onClick={handleEditAssignment}
                >
                  EDIT
                </button>
                <button
                  className="ml-2 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                  onClick={() => {
                    setEditAssignment(false)
                  }}
                >
                  CANCEL
                </button>
            </div>
        </div>
      )}
      {
        privateChat && 
        <div className="fixed inset-0 flex items-center justify-center">
          <div className="bg-black opacity-75 fixed inset-0"></div>
          <div className="bg-white p-8 z-10 w-96">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Private Chat</h2>
            </div>
            <div className="flex flex-col">
              {privateChat?.messageList.map((message, index) => (
                <div key={index} className="flex mb-2">
                  <div className="flex items-center">
                    <img
                      src={generateAvatarUrl(`${message.sender.firstName}+${message.sender.lastName}`)}
                      alt="Avatar"
                      className="w-8 h-8 rounded-full"
                    />
                  </div>
                  <div className="flex flex-col ml-2">
                    <span className="text-sm font-semibold">
                      {`${message.sender.firstName} ${message.sender.lastName}`}
                      {message.sender.role === role && (
                        <span className="text-xs font-light text-gray-500 ml-1">(You)</span>
                      )}
                    </span>
                    <span>{message.content}</span>
                  </div>
                </div>
              ))}
            </div>
            <div className="flex items-center mt-4">
              <input
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                className="flex-grow border rounded-l p-2"
                placeholder="Type A Message..."
              />
              <button
                className="px-4 py-2 bg-blue-500 text-white rounded-r hover:bg-blue-600 ml-2"
                onClick={handleSendMessage}
              >
                SEND
              </button> 
            </div>
            <div className="flex items-center justify-center mt-4">
              <button
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                onClick={() => setPrivateChat(null)}
              >
                CLOSE
              </button>
            </div>
          </div>
        </div>
      }
      {
        chatAssignment && (
          <div className="fixed inset-0 flex items-center justify-center">
            <div className="bg-black opacity-75 fixed inset-0"></div>
            <div className="bg-white p-8 z-10">
              <div className="grid grid-cols-4 gap-4">
                <div className="p-4 bg-gray-200 text-center font-semibold">No</div>
                <div className="p-4 bg-gray-200 text-center font-semibold">Name</div>
                <div className="p-4 bg-gray-200 text-center font-semibold">Email</div>
                <div className="p-4 bg-gray-200 text-center font-semibold">Action</div>

                {currentUsers.map((user, index) => (
                  <React.Fragment key={user.id}>
                    <div className={`p-4 text-center ${index % 2 === 0 ? 'bg-gray-100' : 'bg-white'}`}>
                      {indexOfFirstUser + index + 1}
                    </div>
                    <div className={`p-4 text-center ${index % 2 === 0 ? 'bg-gray-100' : 'bg-white'}`}>
                      {user.firstName} {user.lastName}
                    </div>
                    <div className={`p-4 text-center ${index % 2 === 0 ? 'bg-gray-100' : 'bg-white'}`}>
                      {user.email}
                    </div>
                    <div className={`p-4 text-center ${index % 2 === 0 ? 'bg-gray-100' : 'bg-white'}`}>
                      <button
                        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                        onClick={() => handleChat(user.id)}
                      >
                        View Chat
                      </button>
                    </div>
                  </React.Fragment>
                ))}
              </div>
              <div className="flex justify-between mt-4">
                <button
                  className={`bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded ${
                    currentPage === 1 ? 'opacity-50 cursor-not-allowed' : ''
                  }`}
                  onClick={handlePrevPage}
                  disabled={currentPage === 1}
                >
                  Previous
                </button>
                {isNextButtonVisible && (
                  <div className="flex justify-end mt-4">
                    <button
                      className={`bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded`}
                      onClick={handleNextPage}
                    >
                      Next
                    </button>
                  </div>
                )}
              </div>
              <div className="flex justify-center mt-4">
                <button
                  className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
                  onClick={() => {
                    setChatAssignment(null)
                    setCurrentPage(1)
                  }}
                >
                  Close
                </button>
              </div>
            </div>
        </div>
        )
      }


    </div>
  );
};

export default Course;
