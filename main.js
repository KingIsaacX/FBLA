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

// Initialize app on DOMContentLoaded
document.addEventListener('DOMContentLoaded', initializeApp);

function initializeApp() {
  initializeHTML();
  initializeEventListeners();
  fetchInitialData();
  checkAuthentication().then(() => {
    loadUserDashboard();
  });
}

// Build HTML Structure dynamically
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

function initializeEventListeners() {
  // Search functionality
  document.getElementById('searchInput').addEventListener('input', handleSearchInput);

  // Filter functionality
  document.getElementById('filterContainer').addEventListener('click', handleFilterClick);

  // Modal controls
  const createBtn = document.getElementById('createListingBtn');
  const modal = document.getElementById('createJobModal');
  const closeBtn = modal.querySelector('.modal-close');

  createBtn.addEventListener('click', () => { modal.style.display = 'flex'; });
  closeBtn.addEventListener('click', () => { modal.style.display = 'none'; });

  document.getElementById('createJobForm').addEventListener('submit', handleCreateJob);

  // Scroll to top
  const scrollTopBtn = document.getElementById('scrollTopBtn');
  window.addEventListener('scroll', handleScroll);
  scrollTopBtn.addEventListener('click', scrollToTop);

  window.addEventListener('click', (e) => {
    if (e.target === modal) {
      modal.style.display = 'none';
    }
  });

  // Apply button clicks
  document.getElementById('jobListings').addEventListener('click', handleApplyClick);

  // Auth Modals
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

  // Register Modal
  const registerModal = document.getElementById('registerModal');
  const registerModalCloseBtn = registerModal.querySelector('.register-modal-close');
  const registerLink = document.getElementById('registerLink');
  registerLink.addEventListener('click', (e) => { e.preventDefault(); registerModal.style.display = 'flex'; });
  registerModalCloseBtn.addEventListener('click', () => { registerModal.style.display = 'none'; document.getElementById('registerForm').reset(); document.getElementById('registerFields').innerHTML = ''; });
  document.getElementById('registerRole').addEventListener('change', handleRegisterRoleChange);
  document.getElementById('registerForm').addEventListener('submit', handleRegister);
  window.addEventListener('click', (e) => {
    if (e.target === registerModal) {
      registerModal.style.display = 'none';
      document.getElementById('registerForm').reset();
      document.getElementById('registerFields').innerHTML = '';
    }
  });

  // Logout
  const logoutLink = document.getElementById('logoutLink');
  logoutLink.addEventListener('click', (e) => {
    e.preventDefault();
    handleLogout();
  });

  // Admin Panel
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

// Show Admin Panel
async function showAdminPanel() {
  const adminPanelModal = document.getElementById('adminPanelModal');
  adminPanelModal.style.display = 'flex';

  try {
    // Let's assume pending postings are those with status = 'PENDING'
    // We can filter from `jobs` or refetch if needed
    const pending = jobs.filter(j => j.status && j.status.toUpperCase() === 'PENDING');

    const adminPendingContainer = document.getElementById('adminPendingPostings');
    adminPendingContainer.innerHTML = '';
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

      adminPendingContainer.addEventListener('click', handleAdminAction);
    }
  } catch (error) {
    console.error('Error loading admin panel:', error);
    showToast('Error loading admin panel', 'error');
  }
}

// Handle Admin Actions
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
      headers: getAuthHeaders()
    });
    showToast('Posting approved!', 'success');
    // Update local jobs array
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
      body: JSON.stringify({ reason })
    });
    showToast('Posting rejected!', 'success');
    // Update local jobs array
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

// Handle Login
async function handleLogin(e) {
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

// Handle Register
async function handleRegister(e) {
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
}

// Handle Logout
async function handleLogout() {
  try {
    await fetchWithErrorHandling(API_ENDPOINTS.AUTH.LOGOUT, { method: 'POST' });
    currentUser = null;
    localStorage.removeItem('authToken');
    showToast('Logged out successfully!', 'success');
    updateUIForAuthenticatedUser();
    document.getElementById('userDashboard') && (document.getElementById('userDashboard').style.display = 'none');
  } catch (error) {
    showToast('Logout failed.', 'error');
  }
}

// Load User Dashboard Based on Role
function loadUserDashboard() {
  if (!currentUser) return;


}

