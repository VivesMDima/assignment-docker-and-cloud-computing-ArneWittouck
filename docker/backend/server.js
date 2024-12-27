const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

// Initialize Express
const app = express();

// Allow requests from the frontend
app.use(cors({ origin: 'http://localhost:3001' }));

// Middleware
app.use(express.json());

// Port Configuration
const port = 8081;

// MongoDB Connection
const connectWithRetry = async () => {
  try {
    await mongoose
      .connect('mongodb://mongodb:27017/orkestHubDB', {
        useNewUrlParser: true,
        useUnifiedTopology: true,
      });
    return console.log('Connected to MongoDB!');
  } catch (error) {
    console.error('Failed to connect to MongoDB - retrying in 1 second...', error);
    setTimeout(connectWithRetry, 1000);
  }
};

const seedMembers = async () => {
  const existingMembers = await Member.find();
  if (existingMembers.length === 0) {
    const members = [
      {
        name: { first: 'John', last: 'Doe' },
        gender: 'Male',
        address: {
          street: 'Main Street',
          number: '123',
          city: 'New York',
          postalcode: 10001,
        },
        email: 'john.doe@example.com',
        phone: '+1-555-555-5555',
        birthdate: new Date('1990-01-01'),
        memberSince: new Date('2020-01-01'),
        instruments: ['Guitar', 'Piano'],
        picture: '',
        isManagement: false,
      },
      {
        name: { first: 'Jane', last: 'Smith' },
        gender: 'Female',
        address: {
          street: 'Broadway',
          number: '456',
          city: 'Los Angeles',
          postalcode: 90001,
        },
        email: 'jane.smith@example.com',
        phone: '+1-555-123-4567',
        birthdate: new Date('1985-06-15'),
        memberSince: new Date('2018-07-10'),
        instruments: ['Violin', 'Drums'],
        picture: '',
        isManagement: true,
      },
    ];

    try {
      await Member.insertMany(members);
      console.log('Database seeded successfully!');
    } catch (error) {
      console.error('Error seeding database:', error);
    }
  }
};

// Call the seed function after connecting to MongoDB
connectWithRetry().then(seedMembers);

// Define the Member Schema
const memberSchema = new mongoose.Schema({
  name: {
    first: String,
    last: String,
  },
  gender: String,
  address: {
    street: String,
    number: String,
    city: String,
    postalcode: Number,
  },
  email: String,
  phone: String,
  birthdate: Date,
  memberSince: Date,
  instruments: [String],
  picture: String,
  isManagement: Boolean,
});

// Create the Member Model
const Member = mongoose.model('Member', memberSchema);

// Endpoint: Get All Members
app.get('/api/members', async (req, res) => {
  try {
    const members = await Member.find();
    res.json(members);
  } catch (error) {
    console.error('Error fetching members:', error);
    res.status(500).json({ message: 'Error fetching members', error });
  }
});

// Start the Server
app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
