package org.usfirst.frc1024.Emmet.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc1024.Emmet.subsystems.Drivetrain;
import org.usfirst.frc1024.Emmet.subsystems.Gripper;
import org.usfirst.frc1024.Emmet.subsystems.Lifter;

/**
 *
 */
public class  Auton extends Command {
Boolean isDone;
    public Auton() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Drivetrain.gyro.reset();
    	Drivetrain.rightEncoder.reset();
    	isDone=false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() { //81 in
    	if(isDone==false){
    	Drivetrain.slideActuator.set(false);
    	Timer.delay(1); //wait to give other robots time to leave
    	Lifter.Down(1); //drop lift to pick up first tote
    	Timer.delay(0.5); //wait for lift to hit lower E-stop
    	Lifter.Up(1); //lift first tote
    	Timer.delay(1); //go back up to clear second tote
    	Lifter.Stop();
    	Drivetrain.DriveStraightInches(0.6, 76); //drive to second tote
    	Drivetrain.DriveStraightInches(0.6, 6);
    	Drivetrain.Stop(); //stop driving
    	Lifter.Down(1); //drop lift to pick up second tote
    	Timer.delay(1); //wait for lift to hit lower E-stop
    	Lifter.Up(1); //lift second tote with first tote
    	Timer.delay(1.15); //go back up to clear third tote
    	Lifter.Stop();
    	Drivetrain.DriveStraightInches(0.5, 66); //drive to third tote
    	Drivetrain.DriveStraightInches(0.5, 6);
    	Drivetrain.Stop(); //stop driving
    	Lifter.Down(1); //drop lift to pick up third tote
    	Timer.delay(1); //wait for lift to hit lower E-stop
    	Lifter.Up(1); //lift third tote with second and first totes
    	Timer.delay(0.5); //go back up to clear ground
    	Lifter.Stop();
    	Drivetrain.turnRight(0.45, 75); //turn right 90 degrees
    	Drivetrain.DriveStraightInches(0.5, 110);//drive into the auto zone
    	Drivetrain.DriveStraightInches(0.5, -6);
    	Drivetrain.Stop(); //stop driving
    	Timer.delay(0.1);
    	Lifter.Down(1); //lower the lift to set down the stack
    	Timer.delay(0.5); //wait for the lift to reach the ground
    	Lifter.Stop();
    	Gripper.Open(); //open the gripper to release stack
    	isDone=true;
    	}
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }


   
}
