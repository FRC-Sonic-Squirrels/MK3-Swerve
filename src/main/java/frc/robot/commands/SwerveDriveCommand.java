package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SwerveDrivetrain;
import frc.robot.subsystems.SwerveModuleMK3;

public class SwerveDriveCommand extends CommandBase {

  private final SwerveDrivetrain drivetrain;
  private final XboxController controller;

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter xspeedLimiter = new SlewRateLimiter(6);
  private final SlewRateLimiter yspeedLimiter = new SlewRateLimiter(6);
  private final SlewRateLimiter rotLimiter = new SlewRateLimiter(6);

  public SwerveDriveCommand(SwerveDrivetrain drivetrain, XboxController controller) {
    this.drivetrain = drivetrain;
    addRequirements(drivetrain);

    this.controller = controller;
  }

  @Override
  public void execute() {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    double joyY = controller.getY(GenericHID.Hand.kLeft);
    if (Math.abs(joyY) < 0.02) {
      joyY = 0.0;
    }
    double xSpeed = 
      -xspeedLimiter.calculate(joyY)
        * SwerveDrivetrain.kMaxSpeed;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    double joyX = controller.getX(GenericHID.Hand.kLeft);
    if (Math.abs(joyX) < 0.02) {
      joyX = 0.0;
    }
    double ySpeed =
      -yspeedLimiter.calculate(joyX)
        * SwerveDrivetrain.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    double rot =
      -rotLimiter.calculate(controller.getX(GenericHID.Hand.kRight))
        * SwerveDrivetrain.kMaxAngularSpeed;

    boolean calibrate = controller.getBumper(GenericHID.Hand.kLeft);

    // TODO: undo nerfing speed
    double nerfStrafe = 0.1;
    double nerfRotation = 0.25;
    xSpeed = nerfStrafe * xSpeed;
    ySpeed = nerfStrafe * ySpeed;
    rot = nerfRotation * rot;

    drivetrain.drive(xSpeed, ySpeed, rot, true, calibrate);
    
  }

}
