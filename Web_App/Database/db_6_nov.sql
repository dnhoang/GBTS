USE [master]
GO
/****** Object:  Database [GreenBus]    Script Date: 11/6/2016 11:43:36 PM ******/
CREATE DATABASE [GreenBus]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'GreenBus', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\GreenBus.mdf' , SIZE = 3072KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'GreenBus_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\GreenBus_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [GreenBus] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [GreenBus].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [GreenBus] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [GreenBus] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [GreenBus] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [GreenBus] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [GreenBus] SET ARITHABORT OFF 
GO
ALTER DATABASE [GreenBus] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [GreenBus] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [GreenBus] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [GreenBus] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [GreenBus] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [GreenBus] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [GreenBus] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [GreenBus] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [GreenBus] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [GreenBus] SET  DISABLE_BROKER 
GO
ALTER DATABASE [GreenBus] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [GreenBus] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [GreenBus] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [GreenBus] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [GreenBus] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [GreenBus] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [GreenBus] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [GreenBus] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [GreenBus] SET  MULTI_USER 
GO
ALTER DATABASE [GreenBus] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [GreenBus] SET DB_CHAINING OFF 
GO
ALTER DATABASE [GreenBus] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [GreenBus] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [GreenBus] SET DELAYED_DURABILITY = DISABLED 
GO
USE [GreenBus]
GO
/****** Object:  Table [dbo].[BusRoute]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BusRoute](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](10) NOT NULL,
	[Name] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_BusRoute] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Card]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Card](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[UniqueIdentifier] [nvarchar](50) NOT NULL,
	[CardName] [nvarchar](50) NULL,
	[RegistrationDate] [datetime] NOT NULL,
	[Balance] [float] NOT NULL,
	[Status] [int] NOT NULL,
	[UserId] [int] NULL,
	[DataVersion] [bigint] NULL,
 CONSTRAINT [PK_Card] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[CreditPlan]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CreditPlan](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Price] [int] NOT NULL,
	[Status] [int] NOT NULL,
 CONSTRAINT [PK_CreditPlan] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OfferSubscription]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OfferSubscription](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](250) NULL,
	[Code] [nvarchar](50) NOT NULL,
	[Price] [int] NOT NULL,
	[DiscountPercent] [float] NOT NULL,
	[TicketNumber] [int] NOT NULL,
	[Status] [int] NOT NULL,
 CONSTRAINT [PK_OfferSubscription] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[PaymentTransaction]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PaymentTransaction](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CardId] [int] NOT NULL,
	[CreditPlanId] [int] NOT NULL,
	[TransactionId] [nvarchar](50) NOT NULL,
	[Total] [int] NOT NULL,
	[PaymentDate] [datetime] NOT NULL,
 CONSTRAINT [PK_PaymentTransaction] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Promotion]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Promotion](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](250) NOT NULL,
	[Description] [nvarchar](max) NOT NULL,
	[Status] [int] NOT NULL,
	[CreatedDate] [datetime] NOT NULL,
	[ExpiredDate] [datetime] NOT NULL,
 CONSTRAINT [PK_Promotion] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Role]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Role](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_Role] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[ScratchCards]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ScratchCards](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](50) NOT NULL,
	[Price] [int] NOT NULL,
	[Status] [int] NOT NULL,
 CONSTRAINT [PK_ScratchCards] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Ticket]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Ticket](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CardId] [int] NULL,
	[TicketTypeId] [int] NULL,
	[BusRouteId] [int] NOT NULL,
	[Total] [int] NOT NULL,
	[BoughtDated] [datetime] NOT NULL,
	[IsNoCard] [bit] NOT NULL,
 CONSTRAINT [PK_Ticket] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[TicketType]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TicketType](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[Price] [int] NOT NULL,
	[Status] [int] NOT NULL,
 CONSTRAINT [PK_TicketType] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Token]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Token](
	[TheKey] [nvarchar](50) NOT NULL,
	[TheValue] [nvarchar](50) NULL,
 CONSTRAINT [PK_Token] PRIMARY KEY CLUSTERED 
(
	[TheKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[User]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[User](
	[UserId] [int] IDENTITY(1,1) NOT NULL,
	[PhoneNumber] [nvarchar](11) NOT NULL,
	[Password] [nvarchar](50) NOT NULL,
	[Fullname] [nvarchar](50) NULL,
	[Status] [int] NOT NULL,
	[RoleId] [int] NOT NULL,
	[NotificationCode] [nvarchar](max) NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[UserId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[UserSubscription]    Script Date: 11/6/2016 11:43:36 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserSubscription](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[UserId] [int] NOT NULL,
	[SubscriptionId] [int] NOT NULL,
	[ExpiredDate] [datetime] NOT NULL,
	[IsActive] [bit] NOT NULL,
	[TicketRemaining] [int] NOT NULL,
 CONSTRAINT [PK_UserSubscription] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET IDENTITY_INSERT [dbo].[BusRoute] ON 

INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (1, N'01', N'Bến Thành- BX Chợ Lớn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (2, N'02', N'Bến Thành- BX Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (3, N'03', N'Bến Thành- Thạnh Lộc')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (4, N'04', N'Bến Thành- Cộng Hòa- An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (5, N'05', N'Bến xe Chợ Lớn - Biên Hòa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (6, N'06', N'Bến xe Chợ Lớn- Đại học Nông Lâm')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (7, N'07', N'Bến xe Chợ Lớn- Gò vấp')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (8, N'08', N'Bến xe Quận 8- Đại học Quốc Gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (9, N'09', N'Chợ Lớn - Bình Chánh - Hưng Long')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (10, N'10', N'Đại học Quốc Gia- Bến xe Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (11, N'11', N'Bến Thành- Đầm Sen')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (12, N'12', N'Bến Thành - Thác Giang Điền')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (13, N'13', N'Bến Thành- Bến xe Củ Chi')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (14, N'14', N'Miền Đông- 3 tháng 2- Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (15, N'15', N'Bến Phú Định- Đầm Sen')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (16, N'16', N'Bến xe Chợ Lớn - Bến xe Tân Phú')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (17, N'17', N'Bến Xe Chợ Lớn - ĐH Sài Gòn - KCX Tân Thuận')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (18, N'18', N'Bến Thành - Chợ Hiệp Thành')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (19, N'19', N'Bến Thành - Khu Chế Xuất Linh Trung - Đại Học Quốc Gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (20, N'20', N'Bến Thành - Nhà Bè')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (21, N'22', N'Bến Xe Quận 8 - KCN Lê Minh Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (22, N'23', N'Bến xe Chợ Lớn - Ngã 3 Giồng - Cầu Lớn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (23, N'24', N'Bến Xe Miền Đông - Hóc Môn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (24, N'25', N'Bến Xe Quận 8 - Khu Dân Cư Vĩnh Lộc A')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (25, N'27', N'Bến Thành - Âu Cơ - An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (26, N'28', N'Bến Thành - Chợ Xuân Thới Thượng')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (27, N'29', N'Bến Phà Cát Lái - Chợ Nông Sản Thủ Đức')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (28, N'30', N'Chợ Tân Hương - Đại học Quốc tế')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (29, N'31', N'Khu dân cư Tân Quy - Bến Thành - Khu dân cư Bình Lợi')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (30, N'32', N'Bến Xe Miền Tây - Bến Xe Ngã Tư Ga')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (31, N'33', N'Bến Xe An Sương - Suối Tiên - Đại học Quốc Gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (32, N'34', N'Bến Thành - Đại Học Công Nghệ Sài Gòn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (33, N'35', N'Tuyến xe buýt Quận 1 - Quận 2')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (34, N'36', N'Bến Thành - Thới An')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (35, N'37', N'Cảng Quận 4 - Nhơn Đức')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (36, N'38', N'KDC Tân Quy - Đầm Sen')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (37, N'39', N'Bến Thành - Võ Văn Kiệt - Bến Xe Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (38, N'40', N'Bến xe Miền Đông- Bến xe Ngã Tư Ga')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (39, N'41', N'Bến xe Miền Tây- Ngã tư Bốn xã- Bến xe An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (40, N'42', N'Chợ Cầu Muối-Chợ nông sản Thủ Đức')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (41, N'43', N'Bến xe Miền Đông- Phà Cát Lái')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (42, N'44', N'Cảng Quận 4- Bình Quới')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (43, N'45', N'Bến xe Quận 8- Bến Thành- Bến xe Miền Đông')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (44, N'46', N'Cảng Quận 4- Bến Thành- Bến Mễ Cốc')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (45, N'47', N'Bến xe Chợ Lớn - Quốc lộ 50 - Hưng Long')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (46, N'48', N'Bến xe Tân Phú - Chợ Hiệp Thành')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (47, N'50', N'Đại học Bách khoa - Đại học Quốc gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (48, N'51', N'Bến xe Miền Đông - Bình Hưng Hòa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (49, N'52', N'Bến Thành - Đại học Quốc tế')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (50, N'53', N'Lê Hồng Phong - Đại học Quốc gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (51, N'54', N'Bến xe Miền Đông - Bến xe Chợ Lớn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (52, N'55', N'Công viên phần mềm Quang Trung - Khu Công nghệ cao (Q9)')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (53, N'56', N'Bến xe Chợ Lớn - Đại học Giao thông Vận tải')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (54, N'57', N'Chợ Phước Bình - Trường THPT Hiệp Bình')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (55, N'58', N'Bến xe Ngã 4 Ga - Bình Mỹ')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (56, N'59', N'Bến xe Quận 8 - Bến xe Ngã 4 Ga')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (57, N'60', N'Bến xe An Sương - KCN Lê Minh Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (58, N'60-1', N'BX Miền Tây - BX Biên Hòa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (59, N'60-3', N'Bến xe Miền Đông - Khu Công nghiệp Nhơn Trạch')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (60, N'60-4', N'Bến xe Miền Đông - Bến xe Hố Nai')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (61, N'61', N'Bến xe Chợ Lớn - KCN Lê Minh Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (62, N'61-1', N'Thủ Đức - Dĩ An')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (63, N'61-3', N'Bến xe An Sương - Thủ Dầu Một')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (64, N'61-4', N'Bến Dược - Dầu Tiếng')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (65, N'61-6', N'Bến Thành - Khu Du lịch Đại Nam')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (66, N'61-7', N'Bến đò Bình Mỹ - Bến xe Bình Dương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (67, N'61-8', N'Bến xe Miền Tây - Khu Du lịch Đại Nam')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (68, N'62', N'Bến xe Quận 8 -Thới An')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (69, N'62-2', N'Bến xe Chợ Lớn - Ngã 3 Tân Lân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (70, N'62-3', N'Bến Củ Chi - Bến xe Hậu Nghĩa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (71, N'62-4', N'Thị trấn Tân Túc - Chợ Bến Lức')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (72, N'62-5', N'Bến xe An Sương - Bến xe Hậu Nghĩa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (73, N'62-7', N'Bến xe Chợ Lớn - Bến xe Đức Huệ')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (74, N'62-8', N'Bến xe Chợ Lớn - Bến xe Tân An')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (75, N'62-9', N'Bến xe Quận 8 - Cầu Nổi')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (76, N'62-10', N'Bến xe Chợ Lớn - Thanh Vĩnh Đông')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (77, N'62-11', N'Bến xe Miền Tây - Tân Tập')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (78, N'64', N'Bến xe Miền Đông - Đầm Sen')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (79, N'65', N'Bến Thành - CMT8 - Bến xe An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (80, N'66', N'Bến xe Chợ Lớn - Bến xe An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (81, N'68', N'Bến xe Chợ Lớn - KCX Tân Thuận')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (82, N'69', N'Công viên 23/9 - KCN Tân Bình')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (83, N'70', N'Tân Quy - Bến Súc')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (84, N'70-1', N'Bến xe Củ Chi - Bến xe Gò Dầu')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (85, N'70-2', N'BX Củ Chi - Hòa Thành')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (86, N'70-3', N'Bến Thành - Mộc Bài')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (87, N'70-5', N'Bố Heo - Lộc Hưng')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (88, N'71', N'Bến xe An Sương - Phật Cô Đơn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (89, N'72', N'Công viên 23/9 - Hiệp Phước')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (90, N'73', N'Chợ Bình Chánh - KCN Lê Minh Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (91, N'74', N'Bến xe An Sương - Bến xe Củ Chi')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (92, N'75', N'Sài Gòn - Cần Giờ')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (93, N'76', N'Long Phước - Suối Tiên - Đền Vua Hùng')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (94, N'77', N'Đồng Hòa - Cần Thạnh')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (95, N'78', N'Thới An - Hóc Môn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (96, N'79', N'Bến xe Củ Chi - Đền Bến Dược')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (97, N'81', N'Bến xe Chợ Lớn - Lê Minh Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (98, N'83', N'Bến xe Củ Chi - Cầu Thầy Cai')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (99, N'84', N'Bến xe Chợ Lớn - Tân Túc')
GO
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (100, N'85', N'Bến xe An Sương- Hậu Nghĩa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (101, N'86', N'Bến Thành - Đại học Tôn Đức Thắng')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (102, N'87', N'Bến xe Củ Chi - An Nhơn Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (103, N'88', N'Bến Thành - Chợ Long Phước')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (104, N'89', N'Đại học Nông Lâm - Trường THPT Hiệp Bình')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (105, N'90', N'Phà Bình Khánh - Cần Thạnh')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (106, N'91', N'Bến xe Miền Tây - Chợ nông sản Thủ Đức')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (107, N'93', N'Bến Thành - Đại học Nông Lâm')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (108, N'94', N'Bến xe Chợ Lớn - Bến xe Củ Chi')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (109, N'95', N'Bến xe Miền Đông - KCN Tân Bình')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (110, N'96', N'Bến Thành - Chợ Bình Điền')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (111, N'99', N'Chợ Thạnh Mỹ Lợi - Đại học Quốc gia')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (112, N'100', N'Bến xe Củ Chi - Cầu Tân Thái')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (113, N'101', N'Bến xe Chợ Lớn - Chợ Tân Nhựt')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (114, N'102', N'Bến Thành - Nguyễn Văn Linh - Bến xe Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (115, N'103', N'Bến xe Chợ Lớn - Bến xe Ngã 4 Ga')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (116, N'104', N'Bến xe An Sương - Đại học Nông Lâm')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (117, N'107', N'Bến xe Củ Chi - Bố Heo')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (118, N'109', N'Công viên 23/9 - Sân bay Tân Sơn Nhất')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (119, N'110', N'Phú Xuân - Hiệp Phước')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (120, N'119', N'Sân bay Tân Sơn Nhất - Bến xe Miền Tây')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (121, N'122', N'Bến xe An Sương - Tân Quy')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (122, N'123', N'Phú Mỹ Hưng (khu H) - Quận 1')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (123, N'124', N'Phú Mỹ Hưng (khu S) - Quận 1')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (124, N'126', N'Bến xe Củ Chi - Bình Mỹ')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (125, N'127', N'An Thới Đông - Ngã ba Bà Xán')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (126, N'128', N'Tân Điền - An Nghĩa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (127, N'139', N'Bến xe Miền Tây - Phú Xuân')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (128, N'140', N'Công viên 23/9 - Phạm Thế Hiển - Ba Tơ')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (129, N'141', N'KDL BCR - Long Trường - KCX Linh Trung 2')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (130, N'144', N'Bến xe Miền Tây - Chợ Lớn - CV Đầm Sen - CX Nhiêu Lộc')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (131, N'145', N'Bến xe Chợ Lớn - Chợ Hiệp Thành')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (132, N'146', N'Bến xe Miền Đông - Chợ Hiệp Thành')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (133, N'148', N'Bến xe Miền Tây - Gò Vấp')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (134, N'149', N'Công viên 23/9 - Khu dân cư Bình Hưng Hòa')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (135, N'150', N'Bến xe Chợ Lớn - Ngã 3 Tân Vạn')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (136, N'151', N'Bến xe Miền Tây - Bến xe An Sương')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (137, N'152', N'Khu dân cư Trung Sơn - Bến Thành - Sân bay Tân Sơn Nhất')
INSERT [dbo].[BusRoute] ([Id], [Code], [Name]) VALUES (138, N'C010', N'Phổ Quang - KCN Hiệp Phước')
SET IDENTITY_INSERT [dbo].[BusRoute] OFF
SET IDENTITY_INSERT [dbo].[Card] ON 

INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (1, N'123456abc', NULL, CAST(N'2016-09-20 00:00:00.000' AS DateTime), 300000, 1, 6, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (2, N'E74458DFR', N'Thẻ đi hàng ngày', CAST(N'2016-10-30 13:23:48.073' AS DateTime), 9000, 1, 4, 21102016111033)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (3, N'DE456XD5', N'Thẻ đi đi học và đi làm', CAST(N'2016-09-25 09:19:04.793' AS DateTime), 5533000, 1, 4, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (4, N'123e987dses', N'Thẻ 123e987dses', CAST(N'2016-10-02 09:56:25.400' AS DateTime), 65000, 1, 10, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (5, N'12rt58ed4r', N'Thẻ đi học', CAST(N'2016-10-02 11:37:19.083' AS DateTime), 45000, 1, 11, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (6, N'154demm04', N'Thẻ 154demm04', CAST(N'2016-10-02 17:45:46.900' AS DateTime), 200000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (7, N'154desse85w', N'Thẻ đi chơi', CAST(N'2016-10-02 11:43:17.587' AS DateTime), 15000, 1, 11, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (8, N'1575s5a822as5', N'Thẻ đi chơi', CAST(N'2016-09-11 00:00:00.000' AS DateTime), 500000, 1, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (9, N'25asa5d5e8', N'Thẻ Hoàng', CAST(N'2016-09-09 00:00:00.000' AS DateTime), 15000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (10, N'DD7F7F81', N'Thẻ của vợ con', CAST(N'2016-09-09 00:00:00.000' AS DateTime), 2158000, 1, 4, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (11, N'XXDA854A', N'Thẻ dsadadsadsadsadsa', CAST(N'2016-10-05 19:43:05.703' AS DateTime), 15000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (12, N'dsdadsdsadsad', N'Thẻ dsdadsdsadsad', CAST(N'2016-10-05 19:41:34.770' AS DateTime), 15000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (13, N'ed125rhyd', N'Thẻ ed125rhyd', CAST(N'2016-10-02 17:47:46.280' AS DateTime), 150000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (14, N'GSBSYYASSASA', N'Thẻ GSBSYYASSASA', CAST(N'2016-10-03 10:39:57.880' AS DateTime), 15000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (15, N'sdsdo5wew8es', N'Thẻ sdsdo5wew8es', CAST(N'2016-10-06 00:55:22.023' AS DateTime), 15000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (16, N'A585CDSS', N'Thẻ sfdssdfđssdffds', CAST(N'2016-10-05 19:40:08.603' AS DateTime), 35000, 0, NULL, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (17, N'DHDHDHDHD', N'Thẻ 8B4A1900', CAST(N'2016-10-21 10:59:21.087' AS DateTime), 2000, 1, 12, 0)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (18, N'DHGGS44T', N'Thẻ DHGGS44T', CAST(N'2016-10-21 11:18:01.970' AS DateTime), 0, 1, 12, 22102016014010)
INSERT [dbo].[Card] ([Id], [UniqueIdentifier], [CardName], [RegistrationDate], [Balance], [Status], [UserId], [DataVersion]) VALUES (19, N'8B4A1900', N'Thẻ 8B4A1900', CAST(N'2016-11-05 18:23:47.510' AS DateTime), 10000, 0, NULL, 0)
SET IDENTITY_INSERT [dbo].[Card] OFF
SET IDENTITY_INSERT [dbo].[CreditPlan] ON 

INSERT [dbo].[CreditPlan] ([Id], [Name], [Price], [Status]) VALUES (2, N'Gói phổ thông', 20000, 1)
INSERT [dbo].[CreditPlan] ([Id], [Name], [Price], [Status]) VALUES (3, N'Gói tối đa', 500000, 1)
INSERT [dbo].[CreditPlan] ([Id], [Name], [Price], [Status]) VALUES (5, N'Gói lâu dài', 100000, 1)
SET IDENTITY_INSERT [dbo].[CreditPlan] OFF
SET IDENTITY_INSERT [dbo].[OfferSubscription] ON 

INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (1, N'Pack Ten', N'TEN', 5000, 30, 10, 1)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (2, N'Pack 20', N'P20', 30000, 50, 20, 1)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (5, N'sdss', N'', 155552, 45, 2, 0)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (6, N'sdad', N'', 123555, 5, 5, 0)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (7, N'dấd', N'', 12000, 10, 50, 0)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (8, N'sdáds', N'', 112255, 10, 1000, 0)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (1003, N'fsdffsaf', N'', 12000, 1, 5, 0)
INSERT [dbo].[OfferSubscription] ([Id], [Name], [Code], [Price], [DiscountPercent], [TicketNumber], [Status]) VALUES (1004, N'dasd', N'', 12554500, 12, 1111, 0)
SET IDENTITY_INSERT [dbo].[OfferSubscription] OFF
SET IDENTITY_INSERT [dbo].[PaymentTransaction] ON 

INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (1, 10, 2, N'14U650088V829180L', 200000, CAST(N'2016-10-09 16:15:15.407' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (2, 3, 3, N'94T48349HL634303K', 500000, CAST(N'2016-10-09 16:19:16.493' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (3, 10, 2, N'09735433DM238113A', 200000, CAST(N'2016-10-09 17:48:56.300' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (4, 10, 5, N'PAY-18X32451H0459092JKO7KFUI', 1000000, CAST(N'2016-10-21 10:16:50.757' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (5, 2, 2, N'PAY-18X32451H0459092JKO7KFUI', 20000, CAST(N'2016-10-21 11:28:50.987' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (6, 2, 2, N'CASH_PAYMENT_BY_STAFF_01649501938', 20000, CAST(N'2016-10-23 04:47:50.123' AS DateTime))
INSERT [dbo].[PaymentTransaction] ([Id], [CardId], [CreditPlanId], [TransactionId], [Total], [PaymentDate]) VALUES (7, 2, 2, N'CASH_PAYMENT_BY_STAFF_01649501938', 20000, CAST(N'2016-10-23 04:48:14.517' AS DateTime))
SET IDENTITY_INSERT [dbo].[PaymentTransaction] OFF
SET IDENTITY_INSERT [dbo].[Promotion] ON 

INSERT [dbo].[Promotion] ([Id], [Name], [Description], [Status], [CreatedDate], [ExpiredDate]) VALUES (4, N'Giảm giá vé ngày Quốc Khánh 2/9', N'...', 0, CAST(N'2016-09-02 00:00:00.000' AS DateTime), CAST(N'2016-12-10 00:00:00.000' AS DateTime))
INSERT [dbo].[Promotion] ([Id], [Name], [Description], [Status], [CreatedDate], [ExpiredDate]) VALUES (5, N'Giảm giá vé ngày Quốc tế phụ nữ', N'...', 0, CAST(N'2017-03-08 00:00:00.000' AS DateTime), CAST(N'2016-12-10 00:00:00.000' AS DateTime))
SET IDENTITY_INSERT [dbo].[Promotion] OFF
SET IDENTITY_INSERT [dbo].[Role] ON 

INSERT [dbo].[Role] ([Id], [Name]) VALUES (1, N'Admin')
INSERT [dbo].[Role] ([Id], [Name]) VALUES (2, N'Manager')
INSERT [dbo].[Role] ([Id], [Name]) VALUES (3, N'Staff')
INSERT [dbo].[Role] ([Id], [Name]) VALUES (4, N'Passenger')
SET IDENTITY_INSERT [dbo].[Role] OFF
SET IDENTITY_INSERT [dbo].[ScratchCards] ON 

INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (418, N'M52050505', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (419, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (420, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (421, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (422, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (423, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (424, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (425, N'M64104407', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (426, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (427, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (428, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (429, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (430, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (431, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (432, N'M78020038', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (433, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (434, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (435, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (436, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (437, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (438, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (439, N'M80173940', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (440, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (441, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (442, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (443, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (444, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (445, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (446, N'M01227841', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (447, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (448, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (449, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (450, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (451, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (452, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (453, N'M06143573', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (454, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (455, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (456, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (457, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (458, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (459, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (460, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (461, N'M17296375', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (462, N'M21113006', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (463, N'M21113006', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (464, N'M21113006', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (465, N'M21113006', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (466, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (467, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (468, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (469, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (470, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (471, N'M33266908', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (472, N'M55310819', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (473, N'M55310819', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (474, N'M55310819', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (475, N'M55310819', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (476, N'M55310819', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (477, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (478, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (479, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (480, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (481, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (482, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (483, N'M59236441', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (484, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (485, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (486, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (487, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (488, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (489, N'M60389342', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (490, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (491, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (492, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (493, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (494, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (495, N'M75206074', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (496, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (497, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (498, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (499, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (500, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (501, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (502, N'M86359976', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (503, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (504, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (505, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (506, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (507, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (508, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (509, N'M08403877', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (510, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (511, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (512, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (513, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (514, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (515, N'M02329419', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (516, N'M14472310', 20000, 1)
GO
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (517, N'M14472310', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (518, N'M91470260', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (519, N'M91470260', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (520, N'M91470260', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (521, N'M91470260', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (522, N'M13524162', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (523, N'M13524162', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (524, N'M13524162', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (525, N'M13524162', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (526, N'M25677063', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (527, N'M25677063', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (528, N'M25677063', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (529, N'M25677063', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (530, N'M25677063', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (531, N'M29594695', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (532, N'M29594695', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (533, N'M29594695', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (534, N'M29594695', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (535, N'M29594695', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (536, N'M40647597', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (537, N'M40647597', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (538, N'M40647597', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (539, N'M40647597', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (540, N'M40647597', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (541, N'M44563228', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (542, N'M44563228', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (543, N'M44563228', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (544, N'M44563228', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (545, N'M66617130', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (546, N'M66617130', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (547, N'M66617130', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (548, N'M66617130', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (549, N'M78760031', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (550, N'M78760031', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (551, N'M78760031', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (552, N'M78760031', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (553, N'M78760031', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (554, N'M72687663', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (555, N'M72687663', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (556, N'M72687663', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (557, N'M72687663', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (558, N'M72687663', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (559, N'M94830564', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (560, N'M94830564', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (561, N'M94830564', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (562, N'M98756296', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (563, N'M98756296', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (564, N'M98756296', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (565, N'M98756296', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (566, N'M98756296', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (567, N'M19800198', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (568, N'M19800198', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (569, N'M19800198', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (570, N'M19800198', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (571, N'M19800198', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (572, N'M21943009', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (573, N'M21943009', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (574, N'M21943009', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (575, N'M21943009', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (576, N'M25870631', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (577, N'M25870631', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (578, N'M25870631', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (579, N'M25870631', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (580, N'M47913532', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (581, N'M47913532', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (582, N'M47913532', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (583, N'M47913532', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (584, N'M41849264', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (585, N'M41849264', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (586, N'M41849264', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (587, N'M41849264', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (588, N'M41849264', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (589, N'M53983165', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (590, N'M53983165', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (591, N'M53983165', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (592, N'M53983165', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (593, N'M74036977', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (594, N'M74036977', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (595, N'M74036977', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (596, N'M74036977', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (597, N'M78953609', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (598, N'M78953609', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (599, N'M78953609', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (600, N'M78953609', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (601, N'M90006500', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (602, N'M90006500', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (603, N'M90006500', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (604, N'M90006500', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (605, N'M94922132', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (606, N'M94922132', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (607, N'M94922132', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (608, N'M94922132', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (609, N'M06076033', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (610, N'M06076033', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (611, N'M06076033', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (612, N'M27129935', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (613, N'M27129935', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (614, N'M27129935', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (615, N'M27129935', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (616, N'M22045677', 20000, 1)
GO
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (617, N'M22045677', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (618, N'M20681917', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (619, N'M20681917', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (620, N'M24507548', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (621, N'M24507548', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (622, N'M24507548', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (623, N'M24507548', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (624, N'M36651440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (625, N'M36651440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (626, N'M36651440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (627, N'M36651440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (628, N'M36651440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (629, N'M58704351', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (630, N'M58704351', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (631, N'M58704351', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (632, N'M58704351', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (633, N'M52621083', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (634, N'M52621083', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (635, N'M52621083', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (636, N'M52621083', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (637, N'M52621083', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (638, N'M73874984', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (639, N'M73874984', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (640, N'M73874984', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (641, N'M73874984', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (642, N'M78790516', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (643, N'M78790516', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (644, N'M78790516', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (645, N'M78790516', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (646, N'M78790516', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (647, N'M89844418', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (648, N'M89844418', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (649, N'M89844418', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (650, N'M89844418', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (651, N'M89844418', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (652, N'M01997319', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (653, N'M01997319', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (654, N'M01997319', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (655, N'M01997319', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (656, N'M05813951', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (657, N'M05813951', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (658, N'M05813951', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (659, N'M05813951', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (660, N'M27967852', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (661, N'M27967852', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (662, N'M27967852', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (663, N'M27967852', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (664, N'M27967852', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (665, N'M21883584', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (666, N'M21883584', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (667, N'M21883584', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (668, N'M21883584', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (669, N'M21883584', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (670, N'M32937485', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (671, N'M32937485', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (672, N'M32937485', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (673, N'M32937485', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (674, N'M54080387', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (675, N'M54080387', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (676, N'M54080387', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (677, N'M54080387', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (678, N'M58906929', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (679, N'M58906929', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (680, N'M58906929', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (681, N'M58906929', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (682, N'M70050820', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (683, N'M70050820', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (684, N'M70050820', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (685, N'M70050820', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (686, N'M70050820', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (687, N'M74976552', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (688, N'M74976552', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (689, N'M74976552', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (690, N'M74976552', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (691, N'M86020453', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (692, N'M86020453', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (693, N'M86020453', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (694, N'M86020453', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (695, N'M07173355', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (696, N'M07173355', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (697, N'M07173355', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (698, N'M07173355', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (699, N'M01099996', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (700, N'M01099996', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (701, N'M01099996', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (702, N'M01099996', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (703, N'M23143898', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (704, N'M23143898', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (705, N'M23143898', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (706, N'M23143898', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (707, N'M27069520', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (708, N'M27069520', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (709, N'M27069520', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (710, N'M27069520', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (711, N'M39113321', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (712, N'M39113321', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (713, N'M39113321', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (714, N'M39113321', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (715, N'M50256223', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (716, N'M50256223', 20000, 1)
GO
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (717, N'M50256223', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (718, N'M42908440', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (719, N'M64253919', 50000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (720, N'M64253919', 100000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (721, N'U509574', 20000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (722, N'N831542', 50000, 1)
INSERT [dbo].[ScratchCards] ([Id], [Code], [Price], [Status]) VALUES (723, N'N831542', 100000, 1)
SET IDENTITY_INSERT [dbo].[ScratchCards] OFF
SET IDENTITY_INSERT [dbo].[Ticket] ON 

INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (1, 3, 1, 1, 5000, CAST(N'2016-09-09 16:17:22.457' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (2, 3, 1, 1, 5000, CAST(N'2016-09-09 16:17:26.170' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (3, 2, 1, 1, 5000, CAST(N'2016-10-09 19:11:17.273' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (4, 2, 1, 1, 5000, CAST(N'2016-10-09 19:12:16.503' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (5, 2, 1, 1, 5000, CAST(N'2016-10-09 19:12:49.803' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (6, 2, 1, 1, 5000, CAST(N'2016-10-09 19:16:13.803' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (7, 2, 1, 1, 5000, CAST(N'2016-10-09 19:17:11.410' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (8, 2, 1, 1, 5000, CAST(N'2016-10-09 19:21:51.870' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (9, 2, 1, 1, 5000, CAST(N'2016-10-09 19:22:51.853' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (10, 2, 1, 1, 5000, CAST(N'2016-10-09 19:29:13.457' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (11, 2, 1, 1, 7000, CAST(N'2016-10-10 19:34:02.787' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (12, 2, 1, 1, 5000, CAST(N'2016-10-20 10:27:21.330' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (13, 2, 1, 1, 5000, CAST(N'2016-10-20 10:27:27.230' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (14, 2, 1, 1, 5000, CAST(N'2016-10-20 10:27:34.597' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (15, 2, 1, 1, 5000, CAST(N'2016-10-20 10:27:36.887' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (16, 2, 1, 1, 5000, CAST(N'2016-10-20 10:33:29.013' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (17, 2, 1, 1, 5000, CAST(N'2016-10-20 10:33:35.133' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (18, 2, 1, 1, 5000, CAST(N'2016-10-20 10:33:38.987' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (19, 2, 1, 1, 5000, CAST(N'2016-10-20 10:33:44.720' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (20, 2, 1, 1, 5000, CAST(N'2016-10-20 10:34:12.037' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (21, 2, 1, 1, 5000, CAST(N'2016-10-20 11:09:41.950' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (22, 2, 1, 1, 5000, CAST(N'2016-10-20 11:09:56.687' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (23, 2, 1, 1, 5000, CAST(N'2016-10-20 11:10:11.790' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (24, 2, 1, 1, 5000, CAST(N'2016-10-20 11:10:33.687' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (25, 17, 1, 8, 5000, CAST(N'2016-10-21 10:36:06.340' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (26, 18, 1, 8, 5000, CAST(N'2016-10-21 11:22:48.400' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (27, 18, 1, 1, 5000, CAST(N'2016-10-25 01:33:44.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (28, 18, 1, 1, 5000, CAST(N'2016-10-25 01:35:01.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (29, 18, 1, 1, 5000, CAST(N'2016-10-25 01:37:40.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (30, 18, 1, 1, 5000, CAST(N'2016-10-25 01:40:10.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (31, NULL, 1, 1, 5000, CAST(N'2016-10-22 20:23:33.597' AS DateTime), 1)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (32, NULL, 1, 1, 5000, CAST(N'2016-10-22 20:28:30.000' AS DateTime), 1)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (33, NULL, 1, 1, 5000, CAST(N'2016-10-21 20:29:17.000' AS DateTime), 1)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (35, 2, 1, 1, 5000, CAST(N'2016-10-30 14:08:59.360' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (36, 2, 1, 1, 5000, CAST(N'2016-10-30 14:09:05.460' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (37, 2, 1, 1, 5000, CAST(N'2016-10-30 14:09:06.697' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (38, 2, 1, 1, 7000, CAST(N'2016-10-30 14:09:07.713' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (39, 2, 1, 1, 7000, CAST(N'2016-10-30 14:10:02.763' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (40, 2, 1, 1, 7000, CAST(N'2016-10-30 14:11:15.340' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (41, 2, 1, 1, 7000, CAST(N'2016-10-30 14:11:21.660' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (42, 2, 1, 1, 7000, CAST(N'2016-10-30 14:12:39.787' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (43, 2, 1, 1, 7000, CAST(N'2016-10-30 14:12:59.377' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (44, 2, 1, 1, 7000, CAST(N'2016-10-30 14:13:18.247' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (45, 2, 1, 1, 5000, CAST(N'2016-10-30 14:22:39.417' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (46, 2, 1, 1, 5000, CAST(N'2016-10-30 14:22:46.923' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (47, 2, 1, 1, 5000, CAST(N'2016-10-30 14:23:48.670' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (48, 2, 1, 1, 5000, CAST(N'2016-10-30 14:25:35.637' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (49, 2, 1, 1, 5000, CAST(N'2016-10-30 14:29:40.500' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (50, 2, 1, 1, 5000, CAST(N'2016-10-30 14:33:41.680' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (51, 2, 1, 1, 5000, CAST(N'2016-10-30 14:33:57.080' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (52, 2, 1, 1, 5000, CAST(N'2016-10-30 14:34:50.040' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (53, 2, 1, 1, 5000, CAST(N'2016-10-30 14:35:34.067' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (54, 2, 1, 1, 5000, CAST(N'2016-10-30 14:42:27.490' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (55, 2, 1, 1, 5000, CAST(N'2016-10-30 14:42:40.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (56, 2, 1, 1, 5000, CAST(N'2016-10-30 14:42:55.490' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (57, 2, 1, 1, 5000, CAST(N'2016-10-30 14:43:21.650' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (58, 2, 1, 1, 5000, CAST(N'2016-10-30 14:44:39.753' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (59, 2, 1, 1, 5000, CAST(N'2016-10-30 14:45:09.937' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (60, 2, 1, 1, 5000, CAST(N'2016-10-30 14:45:21.280' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (61, 2, 1, 1, 5000, CAST(N'2016-10-30 14:46:56.313' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (62, 2, 1, 1, 5000, CAST(N'2016-10-30 14:47:12.727' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (63, 2, 1, 1, 5000, CAST(N'2016-10-30 20:30:26.957' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (64, 18, 1, 1, 5000, CAST(N'2016-10-30 20:30:37.330' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (65, 18, 1, 1, 5000, CAST(N'2016-10-30 20:30:45.837' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (66, 18, 1, 1, 1500, CAST(N'2016-10-30 20:33:02.227' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (67, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:13.003' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (68, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:17.073' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (69, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:18.740' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (70, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:19.893' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (71, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:21.107' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (72, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:30.387' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (73, 18, 1, 1, 3500, CAST(N'2016-10-30 20:34:31.937' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (74, 18, 1, 1, 5000, CAST(N'2016-10-30 20:34:42.343' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (75, 18, 1, 1, 5000, CAST(N'2016-10-30 20:34:44.240' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (76, 18, 1, 1, 5000, CAST(N'2016-10-30 20:34:54.623' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (77, 18, 1, 1, 5000, CAST(N'2016-10-30 20:34:55.827' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (78, 18, 1, 1, 3500, CAST(N'2016-10-30 20:35:06.187' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (79, 18, 1, 1, 2500, CAST(N'2016-10-30 20:37:56.510' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (80, 18, 1, 1, 2500, CAST(N'2016-10-30 20:38:00.163' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (81, 18, 1, 1, 2500, CAST(N'2016-10-30 20:38:02.113' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (82, 18, 1, 1, 2500, CAST(N'2016-10-30 20:40:05.117' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (83, 18, 1, 1, 2500, CAST(N'2016-10-30 20:40:08.170' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (84, 18, 1, 1, 2500, CAST(N'2016-10-30 20:40:23.963' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (85, 18, 1, 1, 2500, CAST(N'2016-10-30 20:41:04.250' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (86, 18, 1, 1, 5000, CAST(N'2016-10-30 20:42:13.627' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (87, 18, 1, 1, 3500, CAST(N'2016-10-30 20:42:51.870' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (88, 18, 1, 1, 3500, CAST(N'2016-10-30 20:42:56.683' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (89, 18, 1, 1, 5000, CAST(N'2016-10-30 20:43:12.857' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (90, 18, 1, 1, 5000, CAST(N'2016-10-30 20:51:23.597' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (91, 18, 1, 1, 3500, CAST(N'2016-10-30 20:52:02.207' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (92, 18, 1, 1, 3500, CAST(N'2016-10-30 20:52:05.120' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (93, 18, 1, 1, 3500, CAST(N'2016-10-30 20:52:19.187' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (94, 18, 1, 1, 3500, CAST(N'2016-10-30 20:52:20.857' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (95, 18, 1, 1, 3500, CAST(N'2016-10-30 20:52:21.953' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (96, 18, 1, 1, 3500, CAST(N'2016-10-30 20:53:14.367' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (97, 18, 1, 1, 3500, CAST(N'2016-10-30 20:53:16.383' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (98, 18, 1, 1, 3500, CAST(N'2016-10-30 20:53:17.870' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (99, 18, 1, 1, 3500, CAST(N'2016-10-30 20:53:19.720' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (100, 18, 1, 1, 3500, CAST(N'2016-10-30 20:53:27.913' AS DateTime), 0)
GO
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (101, 18, 1, 1, 5000, CAST(N'2016-10-30 20:53:31.430' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (102, 18, 1, 1, 3500, CAST(N'2016-11-01 13:55:50.057' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (103, 2, 1, 1, 5000, CAST(N'2016-11-04 14:42:49.000' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (104, 2, 1, 1, 5000, CAST(N'2016-11-04 14:43:14.343' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (105, 18, 1, 1, 5000, CAST(N'2016-11-05 18:18:32.793' AS DateTime), 0)
INSERT [dbo].[Ticket] ([Id], [CardId], [TicketTypeId], [BusRouteId], [Total], [BoughtDated], [IsNoCard]) VALUES (106, 19, 1, 1, 5000, CAST(N'2016-11-05 18:29:34.860' AS DateTime), 0)
SET IDENTITY_INSERT [dbo].[Ticket] OFF
SET IDENTITY_INSERT [dbo].[TicketType] ON 

INSERT [dbo].[TicketType] ([Id], [Name], [Description], [Price], [Status]) VALUES (1, N'Vé tuyến ngắn', N'Đồng giá mọi tuyến dưới 18km', 5000, 1)
INSERT [dbo].[TicketType] ([Id], [Name], [Description], [Price], [Status]) VALUES (2, N'Vé tuyến dài', N'Đồng giá mọi tuyến dài trên 18km', 7000, 1)
SET IDENTITY_INSERT [dbo].[TicketType] OFF
INSERT [dbo].[Token] ([TheKey], [TheValue]) VALUES (N'SellTicketToken', N'6f610a11-716a-4c74-8a34-e960a94012d2')
SET IDENTITY_INSERT [dbo].[User] ON 

INSERT [dbo].[User] ([UserId], [PhoneNumber], [Password], [Fullname], [Status], [RoleId], [NotificationCode]) VALUES (4, N'01212184802', N'e10adc3949ba59abbe56e057f20f883e', N'Tran Quang Truong', 1, 4, N'cnxHIAC-YWI:APA91bGCbvX5WBsFodb8U_qXg87cRsnQ2vz2O6uN9kvWLKtlsFMbOYOJPwTk16XjnZLngOHCinj_31c--nkM9S_qyuIXzIiaV5g07MPpZI-yqUB5niMO30A9rj7ET3KbOQa78mNZFMJl')
INSERT [dbo].[User] ([UserId], [PhoneNumber], [Password], [Fullname], [Status], [RoleId], [NotificationCode]) VALUES (6, N'01993916489', N'0308c6c3784163bee30f91bf805875d8', N'Thanh Le', 1, 4, NULL)
INSERT [dbo].[User] ([UserId], [PhoneNumber], [Password], [Fullname], [Status], [RoleId], [NotificationCode]) VALUES (10, N'01649501935', N'e10adc3949ba59abbe56e057f20f883e', N'HoangDN Manager', 1, 2, NULL)
INSERT [dbo].[User] ([UserId], [PhoneNumber], [Password], [Fullname], [Status], [RoleId], [NotificationCode]) VALUES (11, N'01649501939', N'e10adc3949ba59abbe56e057f20f883e', N'Hoang Do Manager', 1, 3, N'cNXxpCOm830:APA91bFRzZMz5m5FX6OOhQ7bKr5JMl8xn8Mkit-KicyHQa9NY9W1trx2WZv3F8Q6CeDJl2zx3qA9O5JIT8p-P3cewN1zCMHGEU3rM79XZGgXagvTOWBAQNZFfvuxqy4TRfLa3YpZKlku')
INSERT [dbo].[User] ([UserId], [PhoneNumber], [Password], [Fullname], [Status], [RoleId], [NotificationCode]) VALUES (12, N'01649501938', N'e10adc3949ba59abbe56e057f20f883e', N'Hoàng', 1, 4, NULL)
SET IDENTITY_INSERT [dbo].[User] OFF
SET IDENTITY_INSERT [dbo].[UserSubscription] ON 

INSERT [dbo].[UserSubscription] ([Id], [UserId], [SubscriptionId], [ExpiredDate], [IsActive], [TicketRemaining]) VALUES (1, 12, 1, CAST(N'2016-11-02 06:00:00.000' AS DateTime), 1, 10)
SET IDENTITY_INSERT [dbo].[UserSubscription] OFF
ALTER TABLE [dbo].[Card]  WITH CHECK ADD  CONSTRAINT [FK_Card_User] FOREIGN KEY([UserId])
REFERENCES [dbo].[User] ([UserId])
GO
ALTER TABLE [dbo].[Card] CHECK CONSTRAINT [FK_Card_User]
GO
ALTER TABLE [dbo].[PaymentTransaction]  WITH CHECK ADD  CONSTRAINT [FK_PaymentTransaction_Card] FOREIGN KEY([CardId])
REFERENCES [dbo].[Card] ([Id])
GO
ALTER TABLE [dbo].[PaymentTransaction] CHECK CONSTRAINT [FK_PaymentTransaction_Card]
GO
ALTER TABLE [dbo].[PaymentTransaction]  WITH CHECK ADD  CONSTRAINT [FK_PaymentTransaction_CreditPlan] FOREIGN KEY([CreditPlanId])
REFERENCES [dbo].[CreditPlan] ([Id])
GO
ALTER TABLE [dbo].[PaymentTransaction] CHECK CONSTRAINT [FK_PaymentTransaction_CreditPlan]
GO
ALTER TABLE [dbo].[Ticket]  WITH CHECK ADD  CONSTRAINT [FK_Ticket_BusRoute] FOREIGN KEY([BusRouteId])
REFERENCES [dbo].[BusRoute] ([Id])
GO
ALTER TABLE [dbo].[Ticket] CHECK CONSTRAINT [FK_Ticket_BusRoute]
GO
ALTER TABLE [dbo].[Ticket]  WITH CHECK ADD  CONSTRAINT [FK_Ticket_Card] FOREIGN KEY([CardId])
REFERENCES [dbo].[Card] ([Id])
GO
ALTER TABLE [dbo].[Ticket] CHECK CONSTRAINT [FK_Ticket_Card]
GO
ALTER TABLE [dbo].[Ticket]  WITH CHECK ADD  CONSTRAINT [FK_Ticket_TicketType] FOREIGN KEY([TicketTypeId])
REFERENCES [dbo].[TicketType] ([Id])
GO
ALTER TABLE [dbo].[Ticket] CHECK CONSTRAINT [FK_Ticket_TicketType]
GO
ALTER TABLE [dbo].[User]  WITH CHECK ADD  CONSTRAINT [FK_User_Role] FOREIGN KEY([RoleId])
REFERENCES [dbo].[Role] ([Id])
GO
ALTER TABLE [dbo].[User] CHECK CONSTRAINT [FK_User_Role]
GO
ALTER TABLE [dbo].[UserSubscription]  WITH CHECK ADD  CONSTRAINT [FK_UserSubscription_OfferSubscription] FOREIGN KEY([SubscriptionId])
REFERENCES [dbo].[OfferSubscription] ([Id])
GO
ALTER TABLE [dbo].[UserSubscription] CHECK CONSTRAINT [FK_UserSubscription_OfferSubscription]
GO
ALTER TABLE [dbo].[UserSubscription]  WITH CHECK ADD  CONSTRAINT [FK_UserSubscription_User] FOREIGN KEY([UserId])
REFERENCES [dbo].[User] ([UserId])
GO
ALTER TABLE [dbo].[UserSubscription] CHECK CONSTRAINT [FK_UserSubscription_User]
GO
USE [master]
GO
ALTER DATABASE [GreenBus] SET  READ_WRITE 
GO
