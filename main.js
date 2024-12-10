// Initialize HTML structure
document.body.innerHTML = `
<nav class="nav">
  <div class="nav-content">
    <a href="#" class="nav-link">Home</a>
    <div class="nav-links">
      <a href="#" class="nav-link">Inbox</a>
      <a href="#" class="nav-link">Backpanel</a>
      <a href="#" class="nav-link">Register</a>
      <a href="#" class="nav-link">Log in</a>
    </div>
  </div>
</nav>
<main class="container">
  <div class="header-section">
    <div class="search-container">
      <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="11" cy="11" r="8"></circle>
        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
      </svg>
      <input type="text" id="searchInput" class="search-input" placeholder="Search for jobs...">
    </div>
    <button id="createListingBtn" class="create-btn">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="12" y1="5" x2="12" y2="19"></line>
        <line x1="5" y1="12" x2="19" y2="12"></line>
      </svg>
      Create New Listing
    </button>
  </div>
  
  <div class="stats-container">
    <div class="stat-card">
      <div class="stat-number" data-target="1500">0</div>
      <div class="stat-label">Active Jobs</div>
    </div>
    <div class="stat-card">
      <div class="stat-number" data-target="5000">0</div>
      <div class="stat-label">Companies</div>
    </div>
    <div class="stat-card">
      <div class="stat-number" data-target="10000">0</div>
      <div class="stat-label">Students Placed</div>
    </div>
  </div>

  <div class="filter-container" id="filterContainer">
    <button class="filter-tag active" data-category="all">All Jobs</button>
    <button class="filter-tag" data-category="technology">Technology</button>
    <button class="filter-tag" data-category="marketing">Marketing</button>
    <button class="filter-tag" data-category="business">Business</button>
  </div>

  <div id="jobListings" class="job-listings">
    <!-- Job listings will be inserted here -->
  </div>
</main>

<button id="scrollTopBtn" class="scroll-top">↑</button>

<div class="modal" id="createJobModal">
  <div class="modal-content">
    <button class="modal-close">&times;</button>
    <h2>Create New Job Listing</h2>
    <form id="createJobForm">
      <div class="form-group">
        <label for="jobTitle">Job Title</label>
        <input type="text" id="jobTitle" required>
      </div>
      <div class="form-group">
        <label for="company">Company</label>
        <input type="text" id="company" required>
      </div>
      <div class="form-group">
        <label for="location">Location</label>
        <input type="text" id="location" required>
      </div>
      <div class="form-group">
        <label for="jobType">Job Type</label>
        <select id="jobType" required>
          <option value="Full-time">Full-time</option>
          <option value="Part-time">Part-time</option>
          <option value="Internship">Internship</option>
        </select>
      </div>
      <div class="form-group">
        <label for="salary">Salary Range</label>
        <input type="text" id="salary" placeholder="e.g., $50,000 - $70,000" required>
      </div>
      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" rows="4" required></textarea>
      </div>
      <div class="form-group">
        <label for="requirements">Requirements</label>
        <textarea id="requirements" rows="4" required></textarea>
      </div>
      <button type="submit" class="create-btn">Create Listing</button>
    </form>
  </div>
</div>

<div id="backgroundPattern" class="background-pattern"></div>
`;

// Add styles
const styles = document.createElement('style');
styles.textContent = `
  :root {
    --primary-color: #283593;
    --background-color: #f5f7fb;
    --card-background: white;
  }

  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  }

  body {
    background: var(--background-color);
    min-height: 100vh;
    overflow-x: hidden;
  }

  .nav {
    background: var(--primary-color);
    color: white;
    padding: 1rem 0;
    position: sticky;
    top: 0;
    z-index: 100;
    transition: all 0.3s ease;
  }

  .nav-scrolled {
    padding: 0.5rem 0;
    background: rgba(40, 53, 147, 0.95);
    backdrop-filter: blur(10px);
  }

  .nav-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .nav-links {
    display: flex;
    gap: 1.5rem;
  }

  .nav-link {
    color: white;
    text-decoration: none;
    padding: 0.5rem;
    transition: all 0.2s ease;
    position: relative;
  }

  .nav-link::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 0;
    height: 2px;
    background: white;
    transition: all 0.2s ease;
    transform: translateX(-50%);
  }

  .nav-link:hover::after {
    width: 80%;
  }

  .container {
    max-width: 1200px;
    margin: 2rem auto;
    padding: 0 1rem;
  }

  .header-section {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    align-items: center;
  }

  .search-container {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 0.75rem;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    display: flex;
    align-items: center;
    transition: all 0.2s ease;
  }

  .search-container:focus-within {
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    transform: translateY(-1px);
  }

  .search-icon {
    color: #666;
    margin-right: 0.75rem;
  }

  .search-input {
    border: none;
    outline: none;
    font-size: 1rem;
    width: 100%;
  }

  .create-btn {
    background: var(--primary-color);
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-size: 1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.2s ease;
  }

  .create-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(40, 53, 147, 0.2);
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .stat-card {
    background: white;
    padding: 1.5rem;
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    transition: all 0.3s ease;
  }

  .stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  }

  .stat-number {
    font-size: 2rem;
    font-weight: bold;
    color: var(--primary-color);
    margin-bottom: 0.5rem;
  }

  .stat-label {
    color: #666;
  }

  .filter-container {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 2rem;
    flex-wrap: wrap;
  }

  .filter-tag {
    padding: 0.5rem 1rem;
    border: 1px solid #ddd;
    border-radius: 20px;
    background: white;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .filter-tag.active {
    background: var(--primary-color);
    color: white;
    border-color: var(--primary-color);
  }

  .job-listings {
    display: grid;
    gap: 1rem;
  }

  .job-card {
    background: white;
    border-radius: 8px;
    padding: 1.5rem;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
  }

  .job-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  }

  .job-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      800px circle at var(--mouse-x) var(--mouse-y),
      rgba(40, 53, 147, 0.06),
      transparent 40%
    );
    opacity: 0;
    transition: opacity 0.3s;
  }

  .job-card:hover::before {
    opacity: 1;
  }

  .modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    backdrop-filter: blur(4px);
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .modal-content {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    width: 90%;
    max-width: 600px;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;
    animation: modalSlideIn 0.3s ease;
  }

  .modal-close {
    position: absolute;
    top: 1rem;
    right: 1rem;
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #666;
    transition: color 0.2s ease;
  }

  .modal-close:hover {
    color: #333;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
  }

  .form-group input,
  .form-group select,
  .form-group textarea {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    transition: all 0.2s ease;
  }

  .form-group input:focus,
  .form-group select:focus,
  .form-group textarea:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(40, 53, 147, 0.1);
    outline: none;
  }

  .scroll-top {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: var(--primary-color);
    color: white;
    border: none;
    cursor: pointer;
    opacity: 0;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    z-index: 99;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .scroll-top:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  }

  .background-pattern {
    position: fixed;
    width: 100vw;
    height: 100vh;
    pointer-events: none;
    z-index: -1;
    opacity: 0.5;
  }

  .pattern-dot {
    position: absolute;
    width: 4px;
    height: 4px;
    background: rgba(40, 53, 147, 0.1);
    border-radius: 50%;
    animation: float 3s ease-in-out infinite;
  }

  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-20px); }
  }

  @keyframes modalSlideIn {
    from { transform: translateY(20px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
  }

  @media (max-width: 768px) {
    .header-section {
      flex-direction: column;
    }

    .create-btn {
      width: 100%;
      justify-content: center;
    }

    .stats-container {
      grid-template-columns: 1fr;
    }

    .modal-content {
      width: 95%;
      padding: 1rem;
    }
  }
`;

document.head.appendChild(styles);

// Sample job data
const jobsData = [
    {
        id: 1,
        title: 'Software Developer Intern',
        company: 'Tech Corp',
        location: 'Remote',
        type: 'Part-time',
        salary: '$20-25/hr',
        posted: '2 days ago',
        description: 'Join our team as a software developer intern. Work on real projects and gain valuable experience in web development, mobile apps, and cloud computing.',
        requirements: ['JavaScript', 'React', 'Node.js'],
        category: 'technology'
    },
    {
        id: 2,
        title: 'Marketing Assistant',
        company: 'Global Media',
        location: 'Hybrid',
        type: 'Full-time',
        salary: '$40-50k/year',
        posted: '1 day ago',
        description: 'Exciting opportunity to work with our marketing team on digital campaigns and social media strategy.',
        requirements: ['Social Media', 'Content Creation', 'Analytics'],
        category: 'marketing'
    },
    {
        id: 3,
        title: 'Business Development Intern',
        company: 'StartUp Inc',
        location: 'In-office',
        type: 'Part-time',
        salary: '$18-22/hr',
        posted: '3 days ago',
        description: 'Looking for a motivated intern to join our business development team.',
        requirements: ['Research', 'Excel', 'Communication'],
        category: 'business'
    }
];

// Initialize background pattern
const backgroundPattern = document.getElementById('backgroundPattern');
for (let i = 0; i < 50; i++) {
    const dot = document.createElement('div');
    dot.className = 'pattern-dot';
    dot.style.left = `${Math.random() * 100}vw`;
    dot.style.top = `${Math.random() * 100}vh`;
    dot.style.animationDelay = `${Math.random() * 2}s`;
    backgroundPattern.appendChild(dot);
}

// Function to render jobs
function renderJobs(jobs) {
    const container = document.getElementById('jobListings');
    container.innerHTML = '';

    jobs.forEach(job => {
        const jobCard = document.createElement('div');
        jobCard.className = 'job-card';
        jobCard.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: flex-start;">
        <div>
          <h3 style="font-size: 1.25rem; font-weight: 600; color: var(--primary-color); margin-bottom: 0.5rem;">
            ${job.title}
          </h3>
          <div style="color: #666; margin-bottom: 1rem;">
            <span>${job.company}</span> • 
            <span>${job.location}</span> • 
            <span>${job.posted}</span>
          </div>
          <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; margin-bottom: 1rem;">
            <span class="filter-tag">${job.type}</span>
            <span class="filter-tag">${job.salary}</span>
          </div>
        </div>
        <button class="create-btn" onclick="applyToJob(${job.id})">Apply Now</button>
      </div>
      <p style="color: #666; margin-bottom: 1rem;">${job.description}</p>
      <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
        ${job.requirements.map(req => `<span class="filter-tag">${req}</span>`).join('')}
      </div>
    `;
        container.appendChild(jobCard);
    });
}

// Initialize with all jobs
renderJobs(jobsData);

// Search functionality
document.getElementById('searchInput').addEventListener('input', (e) => {
    const searchTerm = e.target.value.toLowerCase();
    const filteredJobs = jobsData.filter(job =>
        job.title.toLowerCase().includes(searchTerm) ||
        job.company.toLowerCase().includes(searchTerm) ||
        job.description.toLowerCase().includes(searchTerm)
    );
    renderJobs(filteredJobs);
});

// Filter functionality
document.getElementById('filterContainer').addEventListener('click', (e) => {
    if (e.target.classList.contains('filter-tag')) {
        document.querySelectorAll('.filter-tag').forEach(tag => tag.classList.remove('active'));
        e.target.classList.add('active');

        const category = e.target.dataset.category;
        const filteredJobs = category === 'all'
            ? jobsData
            : jobsData.filter(job => job.category === category);

        renderJobs(filteredJobs);
    }
});

// Animate stats numbers
function animateStats() {
    const stats = document.querySelectorAll('.stat-number');
    stats.forEach(stat => {
        const target = parseInt(stat.dataset.target);
        const duration = 2000;
        const increment = target / (duration / 16);
        let current = 0;

        const updateStat = () => {
            current += increment;
            stat.textContent = Math.floor(current).toLocaleString();
            if (current < target) {
                requestAnimationFrame(updateStat);
            } else {
                stat.textContent = target.toLocaleString();
            }
        };

        updateStat();
    });
}

// Initialize stats animation
animateStats();

// Create job modal functionality
const createBtn = document.getElementById('createListingBtn');
const modal = document.getElementById('createJobModal');
const closeBtn = document.querySelector('.modal-close');

createBtn.addEventListener('click', () => {
    modal.style.display = 'flex';
});

closeBtn.addEventListener('click', () => {
    modal.style.display = 'none';
});

window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

// Handle job creation form submission
document.getElementById('createJobForm').addEventListener('submit', (e) => {
    e.preventDefault();

    const newJob = {
        id: jobsData.length + 1,
        title: document.getElementById('jobTitle').value,
        company: document.getElementById('company').value,
        location: document.getElementById('location').value,
        type: document.getElementById('jobType').value,
        salary: document.getElementById('salary').value,
        description: document.getElementById('description').value,
        requirements: document.getElementById('requirements').value.split(',').map(r => r.trim()),
        category: 'technology', // Default category
        posted: 'Just now'
    };

    jobsData.unshift(newJob);
    renderJobs(jobsData);
    modal.style.display = 'none';
    showToast('Job listing created successfully!');
    e.target.reset();
});

// Scroll to top button functionality
const scrollTopBtn = document.getElementById('scrollTopBtn');

window.addEventListener('scroll', () => {
    if (window.scrollY > 300) {
        scrollTopBtn.style.opacity = '1';
    } else {
        scrollTopBtn.style.opacity = '0';
    }
});

scrollTopBtn.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

// Toast notification function
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    padding: 1rem 2rem;
    background: ${type === 'success' ? '#4CAF50' : '#f44336'};
    color: white;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    z-index: 1000;
    animation: slideIn 0.3s ease-out;
  `;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Apply to job function
function applyToJob(jobId) {
    // Here you would typically handle the job application process
    showToast(`Applied to job #${jobId} successfully!`);
}

// Mouse tracking for job card hover effects
document.addEventListener('mousemove', (e) => {
    document.querySelectorAll('.job-card').forEach(card => {
        const rect = card.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        card.style.setProperty('--mouse-x', `${x}px`);
        card.style.setProperty('--mouse-y', `${y}px`);
    });
});