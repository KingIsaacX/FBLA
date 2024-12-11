// Define base URL for API
const BASE_URL = 'http://localhost:7000';

// API endpoints
const API_ENDPOINTS = {
  AUTH: {
    LOGIN: `${BASE_URL}/api/auth/login`,
    REGISTER: {
      STUDENT: `${BASE_URL}/api/auth/register/student`,
      EMPLOYER: `${BASE_URL}/api/auth/register/employer`
    },
    LOGOUT: `${BASE_URL}/api/auth/logout`,
    ME: `${BASE_URL}/api/auth/me`
  },
  POSTINGS: `${BASE_URL}/api/postings`,
  APPLICATIONS: {
    BASE: `${BASE_URL}/api/applications`,
    BY_POSTING: (id) => `${BASE_URL}/api/applications/posting/${id}`,
    BY_USER: (id) => `${BASE_URL}/api/applications/user/${id}`
  },
  ADMIN: {
    APPROVE: (id) => `${BASE_URL}/api/admin/approve/${id}`,
    REJECT: (id) => `${BASE_URL}/api/admin/reject/${id}`
  },
  STATS: `${BASE_URL}/api/stats`
};

// Roles
const userRoles = {
  STUDENT: 'STUDENT',
  EMPLOYER: 'EMPLOYER',
  ADMIN: 'ADMIN'
};

// Global State
let currentUser = null;
let jobs = [];
let stats = {
  activeJobs: 0,
  companies: 0,
  studentsPlaced: 0
};

document.addEventListener('DOMContentLoaded', initializeApp);

function initializeApp() {
  initializeHTML();
  defineGlobalFunctions(); // Define all global functions before adding listeners
  initializeEventListeners();
  fetchInitialData();
  checkAuthentication().then(() => {
    loadUserDashboard();
  });
}

// Build HTML structure
function initializeHTML() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <nav class="nav">
      <div class="nav-content">
        <a href="#" class="nav-link">School Job Board</a>
        <div class="nav-links">
          <a href="#" class="nav-link admin-only" id="backpanelLink" style="display:none;">Backpanel</a>
          <a href="#" class="nav-link student-only" id="inboxLink" style="display:none;">Inbox</a>
          <a href="#" class="nav-link" id="registerLink">Register</a>
          <a href="#" class="nav-link" id="loginLink">Log in</a>
          <a href="#" class="nav-link" id="logoutLink" style="display: none;">Log out</a>
        </div>
      </div>
    </nav>

    <main class="container">
      <div class="header-section">
        <div class="search-container">
          <svg class="search-icon" ... ></svg>
          <input type="text" id="searchInput" class="search-input" placeholder="Search for jobs...">
        </div>
        <button id="createListingBtn" class="create-btn employer-only" style="display:none;">
          <svg ...></svg>
          Create New Listing
        </button>
      </div>

      <div class="stats-container">
        <div class="stat-card">
          <div class="stat-number" id="activeJobsCount">0</div>
          <div class="stat-label">Active Jobs</div>
        </div>
        <div class="stat-card">
          <div class="stat-number" id="companiesCount">0</div>
          <div class="stat-label">Companies</div>
        </div>
        <div class="stat-card">
          <div class="stat-number" id="studentsPlacedCount">0</div>
          <div class="stat-label">Students Placed</div>
        </div>
      </div>

      <div class="filter-container" id="filterContainer">
        <button class="filter-tag active" data-category="all">All Jobs</button>
        <button class="filter-tag" data-category="technology">Technology</button>
        <button class="filter-tag" data-category="marketing">Marketing</button>
        <button class="filter-tag" data-category="business">Business</button>
      </div>

      <div id="jobListings" class="job-listings"></div>
    </main>

    <div id="createJobModal" class="modal">
      <div class="modal-content">
        <button class="modal-close">&times;</button>
        <h2>Create New Job Listing</h2>
        <form id="createJobForm">
          <div class="form-group">
            <label for="jobTitle">Job Title <span class="required">*</span></label>
            <input type="text" id="jobTitle" required>
          </div>
          <div class="form-group">
            <label for="company">Company <span class="required">*</span></label>
            <input type="text" id="company" required>
          </div>
          <div class="form-group">
            <label for="location">Location <span class="required">*</span></label>
            <input type="text" id="location" required>
          </div>
          <div class="form-group">
            <label for="jobType">Job Type</label>
            <select id="jobType">
              <option value="Full-time">Full-time</option>
              <option value="Part-time">Part-time</option>
              <option value="Internship">Internship</option>
              <option value="Contract">Contract</option>
            </select>
          </div>
          <div class="form-group">
            <label for="salary">Salary Range</label>
            <input type="text" id="salary" placeholder="e.g., 50000">
          </div>
          <div class="form-group">
            <label for="description">Description <span class="required">*</span></label>
            <textarea id="description" rows="4" required></textarea>
          </div>
          <div class="form-group">
            <label for="requirements">Required Skills</label>
            <textarea id="requirements" rows="4" placeholder="Enter skills separated by commas"></textarea>
          </div>
          <button type="submit" class="create-btn">Create Listing</button>
        </form>
      </div>
    </div>

    <button id="scrollTopBtn" class="scroll-top">↑</button>

    <div id="loadingSpinner" class="loading-overlay" style="display: none;">
      <div class="loading-spinner"></div>
    </div>

    <div id="toastContainer"></div>

    <!-- Login Modal -->
    <div id="loginModal" class="modal" style="display:none;">
      <div class="modal-content">
        <button class="modal-close login-modal-close">&times;</button>
        <h2>Log In</h2>
        <form id="loginForm">
          <div class="form-group">
            <label for="loginUsername">Username <span class="required">*</span></label>
            <input type="text" id="loginUsername" required>
          </div>
          <div class="form-group">
            <label for="loginPassword">Password <span class="required">*</span></label>
            <input type="password" id="loginPassword" required>
          </div>
          <button type="submit" class="create-btn">Log In</button>
        </form>
      </div>
    </div>

    <!-- Register Modal -->
    <div id="registerModal" class="modal" style="display:none;">
      <div class="modal-content">
        <button class="modal-close register-modal-close">&times;</button>
        <h2>Register</h2>
        <form id="registerForm">
          <div class="form-group">
            <label for="registerRole">Register As <span class="required">*</span></label>
            <select id="registerRole" required>
              <option value="" disabled selected>Select Role</option>
              <option value="STUDENT">Student</option>
              <option value="EMPLOYER">Employer</option>
            </select>
          </div>
          <div id="registerFields"></div>
          <button type="submit" class="create-btn">Register</button>
        </form>
      </div>
    </div>

    <!-- Admin Panel Modal -->
    <div id="adminPanelModal" class="modal" style="display:none;">
      <div class="modal-content">
        <button class="modal-close admin-modal-close">&times;</button>
        <h2>Admin Panel</h2>
        <p>Pending Postings:</p>
        <div id="adminPendingPostings"></div>
      </div>
    </div>
  `;
}

function defineGlobalFunctions() {
  window.handleCreateJob = async function (e) {
    e.preventDefault();

    if (!currentUser || currentUser.role !== userRoles.EMPLOYER) {
      showToast('Only employers can create job listings.', 'error');
      return;
    }

    try {
      showLoadingSpinner();

      const formData = {
        companyName: document.getElementById('company').value.trim(),
        jobTitle: document.getElementById('jobTitle').value.trim(),
        jobDescription: document.getElementById('description').value.trim(),
        jobType: document.getElementById('jobType').value,
        startingSalary: document.getElementById('salary').value.trim(),
        location: document.getElementById('location').value.trim(),
        skills: document.getElementById('requirements').value.trim()
      };

      if (!formData.companyName || !formData.jobTitle || !formData.jobDescription) {
        showToast('Please fill in all required fields', 'error');
        hideLoadingSpinner();
        return;
      }

      const response = await fetch(API_ENDPOINTS.POSTINGS, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeaders()
        },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to create job posting');
      }

      const newJob = await response.json();
      jobs.unshift(newJob);
      renderJobs(jobs);

      document.getElementById('createJobModal').style.display = 'none';
      e.target.reset();

      showToast('Job listing created successfully!');
    } catch (error) {
      console.error('Error creating job:', error);
      showToast(error.message || 'Error creating job listing', 'error');
    } finally {
      hideLoadingSpinner();
    }
  };

  window.handleLogin = async function (e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!username || !password) {
      showToast('Please fill in all required fields', 'error');
      return;
    }

    try {
      const response = await fetchWithErrorHandling(API_ENDPOINTS.AUTH.LOGIN, {
        method: 'POST',
        body: JSON.stringify({ username, password })
      });

      localStorage.setItem('authToken', response.token);
      currentUser = response.user;
      showToast('Logged in successfully!', 'success');
      updateUIForAuthenticatedUser();
      showBasedOnRole();
      loadUserDashboard();
      document.getElementById('loginModal').style.display = 'none';
      document.getElementById('loginForm').reset();
    } catch (error) {
      console.error('Login failed:', error);
      showToast(error.message || 'Login failed.', 'error');
    }
  };

  window.handleRegister = async function (e) {
    e.preventDefault();
    const role = document.getElementById('registerRole').value;

    if (!role) {
      showToast('Please select a role', 'error');
      return;
    }

    let registrationData;
    let endpoint;

    if (role === userRoles.STUDENT) {
      const username = document.getElementById('studentUsername').value.trim();
      const fullName = document.getElementById('studentFullName').value.trim();
      const email = document.getElementById('studentEmail').value.trim();
      const password = document.getElementById('studentPassword').value.trim();

      if (!username || !fullName || !email || !password) {
        showToast('Please fill in all required fields', 'error');
        return;
      }

      endpoint = API_ENDPOINTS.AUTH.REGISTER.STUDENT;
      registrationData = {
        username,
        password,
        email,
        firstName: fullName.split(' ')[0],
        lastName: fullName.split(' ')[1] || ''
      };
    } else if (role === userRoles.EMPLOYER) {
      const username = document.getElementById('employerUsername').value.trim();
      const companyName = document.getElementById('employerCompanyName').value.trim();
      const email = document.getElementById('employerEmail').value.trim();
      const password = document.getElementById('employerPassword').value.trim();

      if (!username || !companyName || !email || !password) {
        showToast('Please fill in all required fields', 'error');
        return;
      }

      endpoint = API_ENDPOINTS.AUTH.REGISTER.EMPLOYER;
      registrationData = { username, password, email, companyName };
    }

    try {
      const response = await fetchWithErrorHandling(endpoint, {
        method: 'POST',
        body: JSON.stringify(registrationData)
      });

      localStorage.setItem('authToken', response.token);
      currentUser = {
        id: response.id,
        username: response.username,
        role: response.role,
        email: registrationData.email
      };

      showToast('Registration successful! Logged in.', 'success');
      updateUIForAuthenticatedUser();
      showBasedOnRole();
      loadUserDashboard();
      document.getElementById('registerModal').style.display = 'none';
      document.getElementById('registerForm').reset();
      document.getElementById('registerFields').innerHTML = '';
    } catch (error) {
      console.error('Registration failed:', error);
      showToast(error.message || 'Registration failed.', 'error');
    }
  };

  window.handleLogout = async function () {
    try {
      await fetchWithErrorHandling(API_ENDPOINTS.AUTH.LOGOUT, { method: 'POST' });
      currentUser = null;
      localStorage.removeItem('authToken');
      showToast('Logged out successfully!', 'success');
      updateUIForAuthenticatedUser();
      const userDashboard = document.getElementById('userDashboard');
      userDashboard && (userDashboard.style.display = 'none');
    } catch (error) {
      showToast('Logout failed.', 'error');
    }
  };
}

// Event Listeners
function initializeEventListeners() {
  const searchInput = document.getElementById('searchInput');
  searchInput.addEventListener('input', handleSearchInput);

  const filterContainer = document.getElementById('filterContainer');
  filterContainer.addEventListener('click', handleFilterClick);

  const createListingBtn = document.getElementById('createListingBtn');
  const createJobModal = document.getElementById('createJobModal');
  const createJobCloseBtn = createJobModal.querySelector('.modal-close');
  createListingBtn.addEventListener('click', () => { createJobModal.style.display = 'flex'; });
  createJobCloseBtn.addEventListener('click', () => { createJobModal.style.display = 'none'; });

  const createJobForm = document.getElementById('createJobForm');
  createJobForm.addEventListener('submit', handleCreateJob);

  const scrollTopBtn = document.getElementById('scrollTopBtn');
  window.addEventListener('scroll', handleScroll);
  scrollTopBtn.addEventListener('click', scrollToTop);

  window.addEventListener('click', (e) => {
    if (e.target === createJobModal) {
      createJobModal.style.display = 'none';
    }
  });

  const jobListings = document.getElementById('jobListings');
  jobListings.addEventListener('click', handleApplyClick);

  const loginModal = document.getElementById('loginModal');
  const loginModalCloseBtn = loginModal.querySelector('.login-modal-close');
  const loginLink = document.getElementById('loginLink');
  loginLink.addEventListener('click', (e) => { e.preventDefault(); loginModal.style.display = 'flex'; });
  loginModalCloseBtn.addEventListener('click', () => { loginModal.style.display = 'none'; document.getElementById('loginForm').reset(); });
  document.getElementById('loginForm').addEventListener('submit', handleLogin);
  window.addEventListener('click', (e) => {
    if (e.target === loginModal) {
      loginModal.style.display = 'none';
      document.getElementById('loginForm').reset();
    }
  });

  const registerModal = document.getElementById('registerModal');
  const registerModalCloseBtn = registerModal.querySelector('.register-modal-close');
  const registerLink = document.getElementById('registerLink');
  registerLink.addEventListener('click', (e) => { e.preventDefault(); registerModal.style.display = 'flex'; });
  registerModalCloseBtn.addEventListener('click', () => {
    registerModal.style.display = 'none';
    document.getElementById('registerForm').reset();
    document.getElementById('registerFields').innerHTML = '';
  });
  document.getElementById('registerRole').addEventListener('change', handleRegisterRoleChange);
  document.getElementById('registerForm').addEventListener('submit', handleRegister);
  window.addEventListener('click', (e) => {
    if (e.target === registerModal) {
      registerModal.style.display = 'none';
      document.getElementById('registerForm').reset();
      document.getElementById('registerFields').innerHTML = '';
    }
  });

  const logoutLink = document.getElementById('logoutLink');
  logoutLink.addEventListener('click', (e) => {
    e.preventDefault();
    handleLogout();
  });

  const backpanelLink = document.getElementById('backpanelLink');
  backpanelLink.addEventListener('click', (e) => {
    e.preventDefault();
    if (currentUser && currentUser.role === userRoles.ADMIN) {
      showAdminPanel();
    } else {
      showToast('Access denied. Admins only.', 'error');
    }
  });

  const adminPanelModal = document.getElementById('adminPanelModal');
  const adminPanelCloseBtn = adminPanelModal.querySelector('.admin-modal-close');
  adminPanelCloseBtn.addEventListener('click', () => {
    adminPanelModal.style.display = 'none';
  });
  window.addEventListener('click', (e) => {
    if (e.target === adminPanelModal) {
      adminPanelModal.style.display = 'none';
    }
  });
}

// Fetch initial data: Stats and Postings
async function fetchInitialData() {
  try {
    const [statsData, jobsData] = await Promise.all([
      fetchWithErrorHandling(API_ENDPOINTS.STATS),
      fetchWithErrorHandling(API_ENDPOINTS.POSTINGS)
    ]);

    stats = statsData;
    jobs = jobsData;
    updateStatsDisplay();
    renderJobs(jobs);
  } catch (error) {
    console.error('Error fetching initial data:', error);
    showToast('Error loading data. Please try again later.', 'error');
  }
}

// Check Authentication
async function checkAuthentication() {
  const token = localStorage.getItem('authToken');
  if (token) {
    try {
      const user = await fetchWithErrorHandling(API_ENDPOINTS.AUTH.ME, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      currentUser = user;
      showBasedOnRole();
      updateUIForAuthenticatedUser();
    } catch (error) {
      console.error('Authentication failed:', error);
      currentUser = null;
      showBasedOnRole();
      updateUIForAuthenticatedUser();
    }
  } else {
    currentUser = null;
    showBasedOnRole();
    updateUIForAuthenticatedUser();
  }
}

// Update UI based on authentication
function updateUIForAuthenticatedUser() {
  const loginLink = document.getElementById('loginLink');
  const registerLink = document.getElementById('registerLink');
  const logoutLink = document.getElementById('logoutLink');
  const createListingBtn = document.getElementById('createListingBtn');
  const backpanelLink = document.getElementById('backpanelLink');

  if (currentUser) {
    loginLink.style.display = 'none';
    registerLink.style.display = 'none';
    logoutLink.style.display = 'block';

    if (currentUser.role === userRoles.EMPLOYER) {
      createListingBtn.style.display = 'flex';
    } else {
      createListingBtn.style.display = 'none';
    }

    if (currentUser.role === userRoles.ADMIN) {
      backpanelLink.style.display = 'block';
    } else {
      backpanelLink.style.display = 'none';
    }

  } else {
    loginLink.style.display = 'block';
    registerLink.style.display = 'block';
    logoutLink.style.display = 'none';
    createListingBtn.style.display = 'none';
    backpanelLink.style.display = 'none';
  }
}

function showBasedOnRole() {
  const userRole = currentUser ? currentUser.role : null;

  document.querySelectorAll('.student-only').forEach(el => {
    el.style.display = userRole === userRoles.STUDENT ? 'block' : 'none';
  });

  document.querySelectorAll('.employer-only').forEach(el => {
    el.style.display = userRole === userRoles.EMPLOYER ? 'block' : 'none';
  });

  document.querySelectorAll('.admin-only').forEach(el => {
    el.style.display = userRole === userRoles.ADMIN ? 'block' : 'none';
  });
}

// Load User Dashboard based on role
function loadUserDashboard() {
  if (!currentUser) return;
  // If you have dashboard elements, show/hide them here
}

// Show Admin Panel (Approve/Reject Postings)
async function showAdminPanel() {
  const adminPanelModal = document.getElementById('adminPanelModal');
  adminPanelModal.style.display = 'flex';

  try {
    const adminPendingContainer = document.getElementById('adminPendingPostings');
    adminPendingContainer.innerHTML = '';

    const pending = jobs.filter(j => j.status && j.status.toUpperCase() === 'PENDING');
    if (pending.length === 0) {
      adminPendingContainer.innerHTML = '<div>No pending postings</div>';
    } else {
      pending.forEach(post => {
        const postDiv = document.createElement('div');
        postDiv.className = 'admin-posting-card';
        postDiv.innerHTML = `
          <h4>${sanitizeHtml(post.jobTitle)} at ${sanitizeHtml(post.companyName)}</h4>
          <p>${sanitizeHtml(post.jobDescription)}</p>
          <button class="approve-posting-btn" data-id="${post.id}">Approve</button>
          <button class="reject-posting-btn" data-id="${post.id}">Reject</button>
        `;
        adminPendingContainer.appendChild(postDiv);
      });

      adminPendingContainer.onclick = handleAdminAction;
    }
  } catch (error) {
    console.error('Error loading admin panel:', error);
    showToast('Error loading admin panel', 'error');
  }
}

// Handle Admin Action
async function handleAdminAction(e) {
  if (e.target.classList.contains('approve-posting-btn')) {
    const postingId = e.target.dataset.id;
    await approvePosting(postingId);
  } else if (e.target.classList.contains('reject-posting-btn')) {
    const postingId = e.target.dataset.id;
    const reason = prompt('Enter rejection reason:', 'Not suitable');
    if (reason) {
      await rejectPosting(postingId, reason);
    }
  }
}

// Approve Posting
async function approvePosting(postingId) {
  try {
    await fetchWithErrorHandling(API_ENDPOINTS.ADMIN.APPROVE(postingId), {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ token: getCurrentToken(), postingId })
    });
    showToast('Posting approved!', 'success');
    const job = jobs.find(j => j.id === postingId);
    if (job) job.status = 'APPROVED';
    showAdminPanel();
    renderJobs(jobs);
  } catch (error) {
    console.error('Error approving posting:', error);
  }
}

// Reject Posting
async function rejectPosting(postingId, reason) {
  try {
    await fetchWithErrorHandling(API_ENDPOINTS.ADMIN.REJECT(postingId), {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ token: getCurrentToken(), postingId, reason })
    });
    showToast('Posting rejected!', 'success');
    const job = jobs.find(j => j.id === postingId);
    if (job) {
      job.status = 'REJECTED';
      job.rejectionReason = reason;
    }
    showAdminPanel();
    renderJobs(jobs);
  } catch (error) {
    console.error('Error rejecting posting:', error);
  }
}

// Handle search input
function handleSearchInput(e) {
  const searchTerm = e.target.value.toLowerCase();
  const filteredJobs = jobs.filter(job =>
    (job.jobTitle || '').toLowerCase().includes(searchTerm) ||
    (job.companyName || '').toLowerCase().includes(searchTerm) ||
    (job.jobDescription || '').toLowerCase().includes(searchTerm) ||
    (job.skills || '').toLowerCase().includes(searchTerm)
  );
  renderJobs(filteredJobs);
}

// Handle filter click
function handleFilterClick(e) {
  if (e.target.classList.contains('filter-tag')) {
    document.querySelectorAll('.filter-tag').forEach(tag => tag.classList.remove('active'));
    e.target.classList.add('active');

    const category = e.target.dataset.category;
    const filteredJobs = category === 'all' ? jobs : jobs.filter(job => job.category && job.category.toLowerCase() === category);
    renderJobs(filteredJobs);
  }
}

// Handle scroll
function handleScroll() {
  const scrollTopBtn = document.getElementById('scrollTopBtn');
  if (window.scrollY > 300) {
    scrollTopBtn.style.opacity = '1';
    scrollTopBtn.style.visibility = 'visible';
  } else {
    scrollTopBtn.style.opacity = '0';
    scrollTopBtn.style.visibility = 'hidden';
  }
}

// Scroll to top
function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Handle apply click
function handleApplyClick(e) {
  if (e.target.classList.contains('apply-btn')) {
    const jobId = e.target.dataset.jobId;
    handleJobApplication(jobId);
  }
}

// Handle Job Application
async function handleJobApplication(jobId) {
  if (!currentUser) {
    showToast('Please log in to apply for jobs', 'error');
    return;
  }
  if (currentUser.role !== userRoles.STUDENT) {
    showToast('Only students can apply for jobs.', 'error');
    return;
  }

  const firstName = prompt("Enter your first name:", "John");
  const lastName = prompt("Enter your last name:", "Doe");
  const phoneNumber = prompt("Enter your phone number:", "123-456-7890");
  const email = prompt("Enter your email:", currentUser.email || "student@example.com");
  const education = prompt("Enter your education:", "B.Sc. in Computer Science");
  const experience = prompt("Enter your experience:", "2 years internship experience");
  const references = prompt("Enter your references:", "None");

  if (!firstName || !lastName || !email) {
    showToast('Missing required fields for application.', 'error');
    return;
  }

  try {
    showLoadingSpinner();

    const applicationData = {
      token: getCurrentToken(),
      postingId: jobId,
      firstName,
      lastName,
      phoneNumber,
      email,
      education,
      experience,
      references
    };

    const response = await fetch(API_ENDPOINTS.APPLICATIONS.BASE + "/submit", {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(applicationData)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to submit application');
    }

    showToast('Application submitted successfully!');
  } catch (error) {
    console.error('Error submitting application:', error);
    showToast(error.message || 'Error submitting application', 'error');
  } finally {
    hideLoadingSpinner();
  }
}

// Fetch with Error Handling
async function fetchWithErrorHandling(url, options = {}) {
  try {
    showLoadingSpinner();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    const response = await fetch(url, { ...options, headers });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || `HTTP error: ${response.status}`);
    }
    if (response.status !== 204) {
      return await response.json();
    }
    return null;
  } catch (error) {
    showToast(error.message, 'error');
    throw error;
  } finally {
    hideLoadingSpinner();
  }
}

// Animate Stats
function animateStats() {
  const statsEls = document.querySelectorAll('.stat-number');
  statsEls.forEach(stat => {
    const target = parseInt(stat.textContent);
    const duration = 2000;
    const increment = target / (duration / 16);
    let current = 0;
    const animate = () => {
      current += increment;
      if (current < target) {
        stat.textContent = Math.floor(current).toLocaleString();
        requestAnimationFrame(animate);
      } else {
        stat.textContent = target.toLocaleString();
      }
    };
    animate();
  });
}

// Sanitize HTML
function sanitizeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Show/Hide loading spinner
function showLoadingSpinner() {
  const spinner = document.getElementById('loadingSpinner');
  if (spinner) spinner.style.display = 'flex';
}

function hideLoadingSpinner() {
  const spinner = document.getElementById('loadingSpinner');
  if (spinner) spinner.style.display = 'none';
}

// Toast Notifications
function showToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);
  toast.offsetHeight; // trigger reflow
  toast.classList.add('visible');
  setTimeout(() => {
    toast.classList.remove('visible');
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 300);
  }, 3000);
}

// Auth Headers and Token
function getAuthHeaders() {
  const token = localStorage.getItem('authToken');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
}

function getCurrentToken() {
  return localStorage.getItem('authToken') ? btoa(`${currentUser.id}:${currentUser.role}:${Date.now()}`) : '';
}

// Handle Register Role Change
function handleRegisterRoleChange(e) {
  const role = e.target.value;
  const registerFields = document.getElementById('registerFields');
  registerFields.innerHTML = '';

  if (role === userRoles.STUDENT) {
    registerFields.innerHTML = `
      <div class="form-group">
        <label>Username <span class="required">*</span></label>
        <input type="text" id="studentUsername" required>
      </div>
      <div class="form-group">
        <label>Full Name <span class="required">*</span></label>
        <input type="text" id="studentFullName" required>
      </div>
      <div class="form-group">
        <label>Email <span class="required">*</span></label>
        <input type="email" id="studentEmail" required>
      </div>
      <div class="form-group">
        <label>Password <span class="required">*</span></label>
        <input type="password" id="studentPassword" required>
      </div>
    `;
  } else if (role === userRoles.EMPLOYER) {
    registerFields.innerHTML = `
      <div class="form-group">
        <label>Username <span class="required">*</span></label>
        <input type="text" id="employerUsername" required>
      </div>
      <div class="form-group">
        <label>Company Name <span class="required">*</span></label>
        <input type="text" id="employerCompanyName" required>
      </div>
      <div class="form-group">
        <label>Email <span class="required">*</span></label>
        <input type="email" id="employerEmail" required>
      </div>
      <div class="form-group">
        <label>Password <span class="required">*</span></label>
        <input type="password" id="employerPassword" required>
      </div>
    `;
  }
}
