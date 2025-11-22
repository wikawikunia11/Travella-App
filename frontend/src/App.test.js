import { render, screen } from '@testing-library/react';
import App from './App';

test('renders message from backend', () => {
  render(<App />);
  const linkElement = screen.getByText(/Loading message from backend/i);
  expect(linkElement).toBeInTheDocument();
});
