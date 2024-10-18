package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Degrees", group="Calibration")
public class CalibrationDegrees extends LinearOpMode {

    private DcMotorEx slideMotor;
    private DcMotor armMotor;
    private Servo wrist;

    // Constants for arm and lift movements
    final double ARM_TICKS_PER_DEGREE = 28 * 250047.0 / 4913.0 * 100.0 / 20.0 * 1/360.0;
    final double LIFT_TICKS_PER_MM = (111132.0 / 289.0) / 120.0;

    private double armPositionDegrees = 0;  // Using degrees for arm position
    private double slidePositionMM = 0;     // Using millimeters for slide position
    private double wristPosition = 1;

    @Override
    public void runOpMode() {
        // Initialize hardware
        slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        wrist = hardwareMap.get(Servo.class, "wrist");

        // Set initial positions
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Calibration steps:
        telemetry.addData("Status", "Ready for Calibration");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Manual control for the slide using right stick Y
            double slideControl = -gamepad1.right_stick_y;
            slidePositionMM += slideControl * 5; // Adjust sensitivity as needed
            slidePositionMM = Range.clip(slidePositionMM, 0, 600); // Set min and max slide positions in mm

            int slideTargetPosition = (int) (slidePositionMM * LIFT_TICKS_PER_MM); // Convert mm to encoder ticks
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
            slideMotor.setTargetPosition(slideTargetPosition);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setPower(0.5);

            // Manual control for the arm using left stick Y
            double armControl = -gamepad1.left_stick_y;
            armPositionDegrees += armControl * 2; // Adjust sensitivity as needed
            armPositionDegrees = Range.clip(armPositionDegrees, 0, 214); // Set min and max arm positions in degrees 4246 ticks

            int armTargetPosition = (int) (armPositionDegrees * ARM_TICKS_PER_DEGREE); // Convert degrees to encoder ticks
            armMotor.setTargetPosition(armTargetPosition);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(0.5);

            // Manual control for the wrist using gamepad buttons
            if (gamepad1.a) {
                wristPosition += 0.01; // Increase wrist position
            } else if (gamepad1.b) {
                wristPosition -= 0.01; // Decrease wrist position
            }
            //wristPosition = Range.clip(wristPosition, 0, 1); // Range for servos is typically between 0 and 1
            wrist.setPosition(wristPosition);

            // Display telemetry data for calibration
            telemetry.addData("Slide Position (mm):", slidePositionMM);
            telemetry.addData("Slide Target Position (ticks):", slideMotor.getTargetPosition());
            telemetry.addData("Slide Current Position (ticks):", slideMotor.getCurrentPosition());

            telemetry.addData("Arm Position (degrees):", armPositionDegrees);
            telemetry.addData("Arm Target Position (ticks):", armMotor.getTargetPosition());
            telemetry.addData("Arm Current Position (ticks):", armMotor.getCurrentPosition());

            telemetry.addData("Wrist Position", wrist.getPosition());
            telemetry.addData("Wrist Direction", wrist.getDirection());
            telemetry.update();
        }
    }
}