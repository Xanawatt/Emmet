package org.usfirst.frc1024.Emmet;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc1024.Emmet.commands.*;
import org.usfirst.frc1024.Emmet.subsystems.*;

public class Robot extends IterativeRobot {
	boolean isdone = false;

	Command autonomousCommand;
	Command teleopStart;
	SendableChooser autoChooser;
	SendableChooser teleopStartChooser;
	
	public static OI oi;
	public static Drivetrain drivetrain;
	public static Lifter lifter;
	public static Gripper gripper;
	public static int position = 0;
	public static int xBoxButton;
	public static int tankDownsize;
	public static int slideDownsize;
	public static int i = 0;
	public static boolean wingsAreFast;
	public static double VIEW_ANGLE = 0;

	public static int redStepDistanceSB = -20;
	public static int blueStepDistanceSB = -20;
	public static int redStepDistanceWU = -47;
	public static int blueStepDistanceWU = -47;
	/**
	 * wheel circumference is 12.7 inches encoder shaft to wheel gear ratio is
	 * 1.22 the encoder counts 250 counts per revolution
	 * 
	 * We combine these numbers to create encoder constants. These constants are
	 * used to allow us to input feet or inches that we want the robot to move
	 * with no additional calculations needed
	 **/
	public static double encoderConstantInches = 250 / (13.2);
	public static double encoderConstantFeet = 250 / ((13.2) / 12);

	public void robotInit() {
		RobotMap.init();
		// server = CameraServer.getInstance();
		// server.setQuality(100);
		// the camera name (ex "cam0") can be found through the roborio web
		// interface
		// server.startAutomaticCapture("cam3");
//		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
//		session = NIVision.IMAQdxOpenCamera("cam3",
//				NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//		NIVision.IMAQdxConfigureGrab(session);
//		NIVision.IMAQdxStartAcquisition(session);

		drivetrain = new Drivetrain();
		lifter = new Lifter();
		gripper = new Gripper();
		oi = new OI();
		autoChooser = new SendableChooser();
		teleopStartChooser = new SendableChooser();
		autoChooser.addDefault("RC grab", new BackupAuton());
		// autoChooser.addObject("Blue RC burglar (SB, Right)", new
		// StepAutonBlueSBR());
		// autoChooser.addObject("Red RC burglar (SB, Right)", new
		// StepAutonRedSBR());
		// autoChooser.addObject("Blue RC buglar (SB, Left)", new
		// StepAutonBlueSBL());
		// autoChooser.addObject("Red RC burglar (SB, Left)", new
		// StepAutonRedSBL());
		// autoChooser.addObject("Red RC burglar (WU, Right)", new
		// StepAutonRedWUR());
		// autoChooser.addObject("Blue RC burglar (WU, Left)", new
		// StepAutonBlueWUL());
		autoChooser.addObject("RC burglar Station 1", new StepAutonWUR());
		autoChooser.addObject("RC burglar Station 3", new StepAutonWUL());
		autoChooser.addObject("Speedy RC burglar Station 1",
				new StepAutonBumpSwitchRight());
		autoChooser.addObject("Speedy RC burglar Station 3",
				new StepAutonBumpSwitchLeft());
		autoChooser.addObject("Old RC burglar", new StepAutonOriginal());
		autoChooser
				.addObject("Backwards RC grab", new BackwardsContainerGrab());
		autoChooser.addObject("Three tote", new Auton());
		autoChooser.addObject("No Auto", new NoAuto());
		autoChooser.addObject("Chase Mark", new ChaseMarkAuton());
		SmartDashboard.putData("Auto mode", autoChooser);
		teleopStartChooser.addDefault("Do nothing", new TeleopNothing());
		teleopStartChooser.addObject("After RC burglar", new TeleopDropWings());
		teleopStartChooser.addObject("After RC grab", new TeleopMoveBack());
		SmartDashboard.putData("Teleop start", teleopStartChooser);

		autoChooser.addObject("Drive Straight", new driveStraight());
	}
	public void disabledInit() {

	}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	public void autonomousInit() {
		Wings.SetSlow();
	/*	autonomousCommand = (Command) autoChooser.getSelected();
		autonomousCommand.start();*/
	}

	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		if (!isdone) {
		}
		isdone = true;
	}

	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		Wings.SetSlow();
		Drivetrain.gyro.reset();
		Drivetrain.rightEncoder.reset();
		Drivetrain.leftEncoder.reset();
		teleopStart = (Command) teleopStartChooser.getSelected();
		teleopStart.start();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		LiveWindow.run();
		if(Robot.oi.logi.getRawButton(2) == true){
			Lifter.lift1.set(-1.0);
			Lifter.lift2.set(-1.0);
			Timer.delay(0.300);
			Lifter.Stop();
		}
		Lifter.lift1.set((Robot.oi.xBox.getRawAxis(1))
				+ (Robot.oi.logi.getRawAxis(1))/* *2/3 */);
		Lifter.lift2.set((-Robot.oi.xBox.getRawAxis(1))
				- (Robot.oi.logi.getRawAxis(1))/* *2/3 */);
		if (Robot.oi.xBox.getRawAxis(2) > 0.5
				|| Robot.oi.xBox.getRawAxis(3) > 0.5
				|| Robot.oi.logi.getRawButton(7) == true
				|| Robot.oi.logi.getRawButton(8) == true) {
			Gripper.rightArm.set(DoubleSolenoid.Value.kReverse);
		} else if (Robot.oi.xBox.getRawButton(5) == true
				|| Robot.oi.xBox.getRawButton(6) == true
				|| Robot.oi.logi.getRawButton(5) == true
				|| Robot.oi.logi.getRawButton(6) == true) {
			Gripper.rightArm.set(DoubleSolenoid.Value.kForward);
		}
		
		/*
		 * else if(Robot.oi.xBox.getRawAxis(3)>0.5){ //Wings.SetFast();
		 * Wings.Down(); } else if(Robot.oi.xBox.getRawButton(6)){
		 * //Wings.SetFast(); Wings.Up(); }
		 */
		if (Robot.oi.rJoy.getRawButton(1) == true) {
			Drivetrain.slideActuator.set(true);
		} else {
			Drivetrain.slideActuator.set(false);
		}
		if (Robot.oi.rJoy.getRawButton(2) == true) {
			Drivetrain.DriveStraightInches(-0.5, -5.5);
			Gripper.Open();
		}
		if (Robot.oi.lJoy.getRawButton(2) == true) {
			Gripper.Open();
			Drivetrain.DriveStraightInches(-0.5, -15.0);
		}
		// if(Robot.oi.xBox.getRawButton(3)==true){
		// i=0;
		// Wings.Down();
		// Timer.delay(.4);
		// while((i<30) && (Robot.oi.xBox.getRawButton(4)==false)){
		// Wings.Down();
		// Timer.delay(0.02);
		// Wings.Up();
		// Timer.delay(0.012);
		// i+=1;
		// }
		// Wings.Down();
		// }
		if (Robot.oi.xBox.getRawButton(4) == true) {
			Wings.SetFast();
			Wings.Up();
		}
		if (Robot.oi.xBox.getRawButton(1) == true) {
			Wings.SetFast();
			Wings.Down();
		}
		if (Robot.oi.xBox.getRawButton(3) == true) {
			Wings.SetSlow();
			Wings.Down();
		}
		/*
		 * double accelZ = RobotMap.accel.getZ(); double accelX =
		 * RobotMap.accel.getX(); double accelY = RobotMap.accel.getY();
		 */
		SmartDashboard.putNumber("gyro", Drivetrain.gyro.getAngle());
		SmartDashboard.putNumber("RightEncoder",
				Drivetrain.rightEncoder.getDistance());
		SmartDashboard.putNumber("LeftEncoder",
				Drivetrain.leftEncoder.getDistance());
		/*
		 * SmartDashboard.putNumber("AccelX", accelX);
		 * SmartDashboard.putNumber("AccelY", accelY);
		 * SmartDashboard.putNumber("AccelZ", accelZ);
		 */

		SmartDashboard.putBoolean("Compressor Value",
				RobotMap.gripperCompressor.getPressureSwitchValue());
		SmartDashboard.putNumber("xBoxDPad", oi.xBox.getPOV(1));

	}

	public void testPeriodic() {
		LiveWindow.run();
		// quarter speed
		// if((Robot.oi.lJoy.getRawButton(1)==true)){
		// Robot.tankDownsize=4;
		// Robot.slideDownsize=4/3;
		// turbo mode
		if ((Robot.oi.lJoy.getRawButton(2) == true)) {
			Robot.tankDownsize = 1;
			Robot.slideDownsize = 1;
			// regular
		} else {
			Robot.tankDownsize = 4 / 3;
			Robot.slideDownsize = 1;
		}
		Drivetrain.DriveMotors((Robot.oi.lJoy.getRawAxis(1))
				/ (Robot.tankDownsize), (Robot.oi.rJoy.getRawAxis(1))
				/ (Robot.tankDownsize), ((Robot.oi.rJoy.getRawAxis(0)))
				/ (Robot.slideDownsize));
		Lifter.lift1.set((Robot.oi.xBox.getRawAxis(1))
				+ (Robot.oi.logi.getRawAxis(1)) /* *2/3 */);
		Lifter.lift2.set((-Robot.oi.xBox.getRawAxis(1))
				- (Robot.oi.logi.getRawAxis(1)) /* *2/3 */);
		if (Robot.oi.xBox.getRawAxis(2) > 0.5
				|| Robot.oi.xBox.getRawAxis(3) > 0.5
				|| Robot.oi.logi.getRawButton(7) == true
				|| Robot.oi.logi.getRawButton(8) == true) {
			Gripper.rightArm.set(DoubleSolenoid.Value.kReverse);
		} else if (Robot.oi.xBox.getRawButton(5) == true
				|| Robot.oi.xBox.getRawButton(6) == true
				|| Robot.oi.logi.getRawButton(5) == true
				|| Robot.oi.logi.getRawButton(6) == true) {
			Gripper.rightArm.set(DoubleSolenoid.Value.kForward);
		}
		/*
		 * else if(Robot.oi.xBox.getRawAxis(3)>0.5){ //Wings.SetFast();
		 * Wings.Down(); } else if(Robot.oi.xBox.getRawButton(6)){
		 * //Wings.SetFast(); Wings.Up(); }
		 */
		if (Robot.oi.rJoy.getRawButton(1) == true) {
			Drivetrain.slideActuator.set(true);
		} else {
			Drivetrain.slideActuator.set(false);
		}
		if (Robot.oi.rJoy.getRawButton(2) == true) {
			Drivetrain.DriveStraightInches(-0.5, -5.5);
			Gripper.Open();
		}
		if (Robot.oi.lJoy.getRawButton(2) == true) {
			Gripper.Open();
			Drivetrain.DriveStraightInches(-0.5, -15.0);
		}
		// if(Robot.oi.xBox.getRawButton(3)==true){
		// i=0;
		// Wings.Down();
		// Timer.delay(.4);
		// while((i<30) && (Robot.oi.xBox.getRawButton(4)==false)){
		// Wings.Down();
		// Timer.delay(0.02);
		// Wings.Up();
		// Timer.delay(0.012);
		// i+=1;
		// }
		// Wings.Down();
		// }
		if (Robot.oi.xBox.getRawButton(4) == true) {
			Wings.SetFast();
			Wings.Up();
		}
//		if (Robot.oi.xBox.getRawButton(1) == true) {
//			Wings.SetFast();
//			Wings.Down();
//		}
		if (Robot.oi.xBox.getRawButton(3) == true) {
			Wings.SetSlow();
			Wings.Down();
		}
	}
}
