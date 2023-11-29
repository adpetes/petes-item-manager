import React, { useState, useEffect } from 'react';
import './countdowntimer.css'

function CountdownTimer() {
  const calculateNextTuesday = () => {
    const now = new Date();
    const daysUntilTuesday = (2 + 7 - now.getDay()) % 7; 
    const nextTuesday = new Date(now);
    nextTuesday.setDate(now.getDate() + daysUntilTuesday);
    nextTuesday.setHours(10, 0, 0, 0); 

    return nextTuesday;
  };

  const calculateTimeRemaining = () => {
    const now = new Date();
    const targetDate = calculateNextTuesday();
    const difference = targetDate - now;

    const days = Math.floor(difference / (1000 * 60 * 60 * 24));
    const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((difference % (1000 * 60)) / 1000);

    const paddedHours = hours.toString().padStart(2, '0');
    const paddedMinutes = minutes.toString().padStart(2, '0');
    const paddedSeconds = seconds.toString().padStart(2, '0');

    return { days: days, hours: paddedHours, minutes: paddedMinutes, seconds: paddedSeconds, targetDate };
  };

  const [timeRemaining, setTimeRemaining] = useState(calculateTimeRemaining());

  useEffect(() => {
    const timer = setInterval(() => {
      const newTimeRemaining = calculateTimeRemaining();
      setTimeRemaining(newTimeRemaining);

      if (newTimeRemaining.targetDate <= new Date()) {
        const nextTuesday = calculateNextTuesday();
        setTimeRemaining(calculateTimeRemaining());
      }
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  return (
    <div className='countdown-container'>
      <div className='countdown-header'> Time Until Reset</div>
      <div className='countdown-timer'>{`${timeRemaining.days} : ${timeRemaining.hours} : ${timeRemaining.minutes} : ${timeRemaining.seconds}`}</div>
      <div className='countdown-timer-text'>
        <span>Days</span>
        <span>Hours</span>
        <span>Mins</span>
        <span>Secs</span>
      </div>
    </div>
  );
};

export default CountdownTimer;