// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;


import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
public class SwerveDrivetrain extends SubsystemBase {

  //these are limits you can change!!!
  // Units.feetToMeters(13.6);  // 20 feet per second
  public static final double kMaxSpeed = Units.feetToMeters(13.6);
  public static final double kMaxAngularSpeed = Math.PI;   // 1/2 rotation per second
  public static double fieldCalibration = 0;

  //this is where you put the angle offsets in degrees you got from the smart dashboard
  public static double frontLeftOffset = -142.4;
  public static double frontRightOffset = -65.4; 
  public static double backLeftOffset = -28.1;
  public static double backRightOffset = -182.7;

  //put your can Id's here!
  public static final int frontLeftDriveId = 1;
  public static final int frontLeftCANCoderId = 21;
  public static final int frontLeftSteerId = 11;
  //put your can Id's here!
  public static final int frontRightDriveId = 2;
  public static final int frontRightCANCoderId = 22;
  public static final int frontRightSteerId = 12;
  //put your can Id's here!
  public static final int backLeftDriveId = 4;
  public static final int backLeftCANCoderId = 24;
  public static final int backLeftSteerId = 14;
  //put your can Id's here!
  public static final int backRightDriveId = 3;
  public static final int backRightCANCoderId = 23;
  public static final int backRightSteerId = 13;

  public static AHRS gyro = new AHRS(SPI.Port.kMXP);

  private SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
    // width = 19 inches, length = 19 inches
    // front left
    new Translation2d(
      Units.inchesToMeters(9.5),
      Units.inchesToMeters(9.5)
    ),
    // front right
    new Translation2d(
      Units.inchesToMeters(9.5),
      Units.inchesToMeters(-9.5)
    ),
    // back left
    new Translation2d(
      Units.inchesToMeters(-9.5),
      Units.inchesToMeters(9.5)
    ),
    // back right
    new Translation2d(
      Units.inchesToMeters(-9.5),
      Units.inchesToMeters(-9.5)
    )
  );

 

  private SwerveModuleMK3[] modules = new SwerveModuleMK3[] {

    new SwerveModuleMK3(new TalonFX(frontLeftDriveId), new TalonFX(frontLeftSteerId), new CANCoder(frontLeftCANCoderId), Rotation2d.fromDegrees(frontLeftOffset)), // Front Left
    new SwerveModuleMK3(new TalonFX(frontRightDriveId), new TalonFX(frontRightSteerId), new CANCoder(frontRightCANCoderId), Rotation2d.fromDegrees(frontRightOffset)), // Front Right
    new SwerveModuleMK3(new TalonFX(backLeftDriveId), new TalonFX(backLeftSteerId), new CANCoder(backLeftCANCoderId), Rotation2d.fromDegrees(backLeftOffset)), // Back Left
    new SwerveModuleMK3(new TalonFX(backRightDriveId), new TalonFX(backRightSteerId), new CANCoder(backRightCANCoderId), Rotation2d.fromDegrees(backRightOffset))  // Back Right

  };

  public SwerveDrivetrain() {
    gyro.reset(); 
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed Speed of the robot in the x direction (forward).
   * @param ySpeed Speed of the robot in the y direction (sideways).
   * @param rot Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the field.
   * @param calibrateGyro button to recalibrate the gyro offset
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative, boolean calibrateGyro) {
    
    if(calibrateGyro){
      gyro.reset(); //recalibrates gyro offset
    }

    SwerveModuleState[] states =
      kinematics.toSwerveModuleStates(
        fieldRelative
          ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(-gyro.getAngle()))
          : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.normalizeWheelSpeeds(states, kMaxSpeed);
    for (int i = 0; i < states.length; i++) {
      //below is a line to comment out from step 5
      modules[i].setDesiredState(states[i]);
      // SmartDashboard.putNumber(String.valueOf(i) + "_desiredstate_angle_deg", states[i].angle.getDegrees());
      // SmartDashboard.putNumber(String.valueOf(i) + "_desiredstate_speedMetersPerSecond", states[i].speedMetersPerSecond); 
    }

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    for (int i = 0; i < modules.length; i++) {
      SmartDashboard.putNumber(String.valueOf(i), modules[i].getRawAngle());
    }
    SmartDashboard.putNumber("gyro Angle", gyro.getAngle());

    // TODO: add odometry
    // m_pose = m_odometry.update(gyro.getAngle(), m_frontLeftModule.getState(), m_frontRightModule.getState(), m_backLeftModule.getState(), m_backRightModule.getState());
    // SmartDashboard.putNumber("Robot Position (X)", m_pose.getX());
    // SmartDashboard.putNumber("Robot Position (Y)", m_pose.getY());
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
